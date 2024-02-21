package de.felix.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    Encrypter encrypter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        encrypter = new Encrypter(getFilesDir());
        ConstraintLayout mainChatLayout = findViewById(R.id.mainChatFrame);

        Chat chat = new Chat(this, mainChatLayout);

//        try {
//            FileInputStream fis = new FileInputStream(new File(getFilesDir(), "test.txt"));
//            try {
//                fis.readAllBytes();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(new File(getFilesDir(), "test.txt"));
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            fos.write("das ist text".getBytes(StandardCharsets.UTF_8));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }
}