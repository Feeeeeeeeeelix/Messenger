package de.felix.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    Encrypter encrypter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        encrypter = new Encrypter();
        ConstraintLayout mainChatLayout = findViewById(R.id.mainChatFrame);

        Chat chat = new Chat(this, mainChatLayout);
    }
}