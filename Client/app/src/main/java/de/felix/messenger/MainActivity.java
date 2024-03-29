package de.felix.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    String clientName;
    File nameFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameFile = new File(getFilesDir(), "client-name.txt");
        getUserName();

    }

    /**
     * Wenn die App zum ersten mal gestartet wird, frage den Benutzer nach seinem Namen.
     * Andernfalls ist der Name in einer Datei schon gespeichert.
     */
    private void getUserName(){
        if (nameFile.exists()){
            try {
                FileInputStream fis = new FileInputStream(nameFile);
                this.clientName = new String(fis.readAllBytes());
                fis.close();

                initChat();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            promptUserForName();
        }
    }

    /**
     * Fragt den Benutzer beim ersten Start der App nach seinem Namen

     */
    private void promptUserForName(){
//       Quelle:  https://stackoverflow.com/questions/10903754/input-text-dialog-android

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Geben Sie Ihren Namen ein:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clientName =  input.getText().toString();

                saveClientName();

                initChat();

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Beim Druck auf "Ok" wird der eingegebene Name gespeichert
     *
     */
    private void saveClientName(){
        try {
            FileOutputStream fos = new FileOutputStream(nameFile);
            fos.write(this.clientName.getBytes(StandardCharsets.UTF_8));
            fos.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * initialisiert den Chat, und zwei Encrypter Klassen
     */
    private void initChat(){

        Encrypter encrypter = new Encrypter(getFilesDir());
        SymmetricEncryption symencrypter = new SymmetricEncryption(getFilesDir());
        ConstraintLayout mainChatLayout = findViewById(R.id.mainChatFrame);
        ImageButton deleteChatButton = findViewById(R.id.DeleteChatButton);

        Chat chat = new Chat(this,clientName, mainChatLayout, deleteChatButton);

    }

}