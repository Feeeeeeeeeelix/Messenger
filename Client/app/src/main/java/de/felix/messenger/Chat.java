package de.felix.messenger;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Chat {

    ConstraintLayout mainChatFrame;
    LinearLayout mainChatLayout;
    ScrollView mainScrollView;
    Button sendMessageButton;
    EditText messageEntry;

    Messages messages;
    MainActivity context;
    Communication client;

    String clientName;

    KeyManager keyManager;

    public Chat(MainActivity context,String clientName, ConstraintLayout mainLayout, Button deleteButton) {

        this.context = context;
        this.clientName = clientName;
        mainChatFrame = mainLayout;
        mainChatLayout = mainLayout.findViewById(R.id.mainMessageLayout);
        mainScrollView = mainLayout.findViewById(R.id.mainScrollView);
        sendMessageButton = mainLayout.findViewById(R.id.button);
        messageEntry = mainLayout.findViewById(R.id.editTextText);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOwnMessage();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteChatContent();
            }
        });



        keyManager = new KeyManager(clientName);
        messages = new Messages(context.getFilesDir(), clientName, keyManager);

        client = new Communication(context, "chat1", this);

        loadMessages();
    }


    private void createOwnMessage(){
//        Get Message Text and create Message Object
        String currentMessage = messageEntry.getText().toString();
        if (currentMessage.equals("")) return;

        Message newMessage = new Message(1);
        newMessage.setText(currentMessage);

//        Place the message on the screen
        placeNewMessage(newMessage);

        byte[] encryptedText = newMessage.getEncrypted(keyManager.getOwnPublicKey());
        long messageTime = newMessage.getCreationTime();

        messages.saveNewMessage(encryptedText, messageTime, 1);

        sendMessage(encryptedText, messageTime);

    }

    private void placeNewMessage(Message message){
//        Create a Layout for the message and place it in the main layout
        MessageLayout messageLayout = message.getLayout(context);
        mainChatLayout.addView(messageLayout);


//        Scroll to bottom
        View lastChild = mainScrollView.getChildAt(mainScrollView.getChildCount() - 1);
        int bottom = lastChild.getBottom() + mainScrollView.getPaddingBottom();
        int sy = mainScrollView.getScrollY();
        int sh = mainScrollView.getHeight();
        int delta = bottom - (sy +sh);

        mainScrollView.scrollBy(0, delta);
    }

    private void sendMessage(byte[] encryptedText, long timeCreated){
        String messageToSend = String.format("%d§%s", timeCreated, new String(encryptedText, StandardCharsets.UTF_8));
        client.publishMessage(messageToSend);
    }

    public void receiveMessage(String receivedString){
        String[] splitted = receivedString.split("§");
        String encryptedText = splitted[1];
        String messageText = Encrypter.decryptString(encryptedText.getBytes(StandardCharsets.UTF_8), keyManager.getOwnPrivateKey());
        Long timeCreated = Long.valueOf(splitted[0]);

        Message receivedMessage = new Message(0, timeCreated);
        receivedMessage.setText(messageText);

        context.runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                        placeNewMessage(receivedMessage);
                                  }
                              }
        );

        messages.saveNewMessage(messageText.getBytes(StandardCharsets.UTF_8), timeCreated, 0);
    }

    public void updateTimeStamps(){
//    TODO: update Timestamps
    }

    private void loadMessages(){
//        Load all the message from the device and show them on the screen
        ArrayList<Message> Messages = messages.loadMessages();

        for (Message newMessage: Messages) {

//        Place the message on the screen
            placeNewMessage(newMessage);
        }
    }

    private void deleteChatContent(){
        messages.deleteAllMessages();
        mainChatLayout.removeAllViews();
    }
}
