package de.felix.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Encrypter encrypter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendButton = findViewById(R.id.button);
        sendButton.setOnClickListener(this);

        encrypter = new Encrypter();
    }

    @Override
    public void onClick(View v) {
        EditText textInputField = findViewById(R.id.editTextText);
        String inputText = textInputField.getText().toString();
        encrypter.encrypt(inputText);
    }
}