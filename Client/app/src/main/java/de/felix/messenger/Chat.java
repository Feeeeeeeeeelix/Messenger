package de.felix.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.util.HebrewCalendar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Chat {

    ConstraintLayout mainChatFrame;
    LinearLayout mainChatLayout;
    ScrollView mainScrollView;
    Button sendMessageButton;
    EditText messageEntry;

    Messages messages;
    Context context;
    Communication client;

    KeyManager keyManager;

    public Chat(Context context, ConstraintLayout mainLayout) {

        this.context = context;
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
//        TODO: unique chat id

        messages = new Messages(context.getFilesDir(), "Felix");

        keyManager = new KeyManager("Felix");

        client = new Communication(context, "chat1", this);

        loadMessages();
    }


    public void createOwnMessage(){
//        Get Message Text and create Message Object
        String currentMessage = messageEntry.getText().toString();
        Message newMessage = new Message(context, 1);
        newMessage.setText(currentMessage);

//        Place the message on the screen
        placeNewMessage(newMessage);

        byte[] encryptedText = newMessage.getEncrypted(keyManager.getOwnPublicKey());
        long messageTime = newMessage.getCreationTime();

        messages.saveNewMessage(encryptedText, messageTime);

        sendMessage(encryptedText, messageTime);

    }

    public void placeNewMessage(Message message){
//        Create a Layout for the message and place it in the main layout
        MessageLayout messageLayout = message.getLayout();
        mainChatLayout.addView(messageLayout);


//        Scroll to bottom
        View lastChild = mainScrollView.getChildAt(mainScrollView.getChildCount() - 1);
        int bottom = lastChild.getBottom() + mainScrollView.getPaddingBottom();
        int sy = mainScrollView.getScrollY();
        int sh = mainScrollView.getHeight();
        int delta = bottom - (sy +sh);

        mainScrollView.scrollBy(0, delta);
    }

    public void sendMessage(byte[] encryptedText, long timeCreated){

        @SuppressLint("DefaultLocale")
        String messageToSend = String.format("%d§%s", timeCreated, new String(encryptedText, StandardCharsets.UTF_8));
        client.publishMessage(messageToSend);
    }

    public void receiveMessage(String receivedString){
        String[] splitted = receivedString.split("§");
        String messageText = splitted[1];
        Long timeCreated = Long.valueOf(splitted[0]);

        Message receivedMessage = new Message(context, 0, timeCreated);
        receivedMessage.setText(messageText);

//        TODO: thread error while receiving message
//        placeNewMessage(receivedMessage);

        messages.saveNewMessage(messageText.getBytes(StandardCharsets.UTF_8), timeCreated);
    }


    public void updateTimeStamps(){
//    TODO: update Timestamps
    }

    public void loadMessages(){
//        Load all the message from the device and show them on the screen
        HashMap<Long, byte[]> Messages = messages.loadMessages();

        for (Map.Entry<Long, byte[]> message: Messages.entrySet()) {
            String messageText = Encrypter.decryptString(message.getValue(), keyManager.getOwnPrivateKey());

            Message newMessage = new Message(context, 0, message.getKey());
            newMessage.setText(messageText);

//        Place the message on the screen
            placeNewMessage(newMessage);
        }
    }
}
