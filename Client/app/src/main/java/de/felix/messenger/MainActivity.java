package de.felix.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    Encrypter encrypter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        encrypter = new Encrypter(getFilesDir());
        ConstraintLayout mainChatLayout = findViewById(R.id.mainChatFrame);

        Chat chat = new Chat(this, mainChatLayout);

        Communication client = new Communication(getApplicationContext(), "open-messenger-application.duckdns.org", 123, "testtopic2", "Pablo", "ETS2isfun");

        /*findViewById(R.id.btn_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.publishMessage("Hello, World!");
            }
        });*/


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