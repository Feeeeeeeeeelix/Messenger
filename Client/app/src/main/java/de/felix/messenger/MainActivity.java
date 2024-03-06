package de.felix.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;

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

    private void promptUserForName(){
//        https://stackoverflow.com/questions/10903754/input-text-dialog-android
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your Name");
/*// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.name_entry_dialog, (ViewGroup) context.getView(), false);
// Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.userNameInput);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);*/

        // Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
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

    private void saveClientName(){
        try {
            FileOutputStream fos = new FileOutputStream(nameFile);
            fos.write(this.clientName.getBytes(StandardCharsets.UTF_8));
            fos.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initChat(){

        Encrypter encrypter = new Encrypter(getFilesDir());
        SymmetricEncryption symencrypter = new SymmetricEncryption(getFilesDir());
        ConstraintLayout mainChatLayout = findViewById(R.id.mainChatFrame);
        Button deleteChatButton = findViewById(R.id.DeleteChatButton);

        Chat chat = new Chat(this,clientName, mainChatLayout, deleteChatButton);

    }

}