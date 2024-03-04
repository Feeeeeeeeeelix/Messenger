package de.felix.messenger;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;

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

    static String JsonKeyContent = "MessageContent";
    static String JsonKeyTime = "TimeCreated";
    static String JsonKeySender = "MessageSender";

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
        Log.i("Chat", "Creating own Message");

//        Get Message Text and create Message Object
        String currentMessage = messageEntry.getText().toString();
        if (currentMessage.equals("")) return;

        Message newMessage = new Message(1);
        newMessage.setText(currentMessage);

//        Place the message on the screen
        placeNewMessage(newMessage);

        messages.saveNewMessage(newMessage, keyManager.getOwnPublicKey());

        sendMessage(newMessage);
    }

    private void placeNewMessage(Message message){
//        Create a Layout for the message and place it in the main layout
        MessageLayout messageLayout = message.getLayout(context);
        mainChatLayout.addView(messageLayout);

        scrollToBottom();
    }

    private void scrollToBottom() {
        mainScrollView.post(new Runnable() {
            @Override
            public void run() {
                mainScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void sendMessage(Message messageToSend){
        PublicKey privateKeyOfPartner = keyManager.getOwnPublicKey(); //TODO: replace with other key
        byte[] encryptedBytes = messageToSend.getEncrypted(privateKeyOfPartner);
        String base64EncodedMessageContent = Base64.getEncoder().encodeToString(encryptedBytes);
        String timeCreated = new String(String.valueOf(messageToSend.getCreationTime()));

        JSONObject obj = new JSONObject();
        try {
            obj.put(JsonKeyContent, base64EncodedMessageContent);
            obj.put(JsonKeyTime, String.valueOf(messageToSend.getCreationTime()));
            obj.put(JsonKeySender, clientName);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        byte[] sendData = obj.toString().getBytes(StandardCharsets.UTF_8);
        client.publishMessage(sendData);
    }

    public void receiveMessage(byte[] receivedBytes){
        Log.i("Chat", String.format("Received Message : %s", receivedBytes));

        JSONObject obj = null;
        String messageContent;
        long timeCreated;
        String messageSender;

        try {
            obj = new JSONObject(new String(receivedBytes));

            messageContent = obj.getString(JsonKeyContent);
            timeCreated = Long.parseLong(obj.getString(JsonKeyTime));
            messageSender  = obj.getString(JsonKeySender);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        byte[] encryptedBytes = Base64.getDecoder().decode(messageContent);
        String messageText = Encrypter.decryptString(encryptedBytes, keyManager.getOwnPrivateKey());


        Message receivedMessage = new Message(0, timeCreated);
        receivedMessage.setText(messageText);

        context.runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                        placeNewMessage(receivedMessage);
                                  }
                              }
        );

        messages.saveNewMessage(receivedMessage, keyManager.getOwnPublicKey());
    }

    public void updateTimeStamps(){
//    TODO: update Timestamps
    }

    private void loadMessages(){
        Log.i("Chat", "loading Messages...");

//        Load all the message from the device and show them on the screen
        ArrayList<Message> Messages = messages.loadMessages();

        for (Message newMessage: Messages) {

//        Place the message on the screen
            placeNewMessage(newMessage);
        }

    }

    private void deleteChatContent(){
        Log.i("Chat", "Deleting Chat Content");

        messages.deleteAllMessages();
        mainChatLayout.removeAllViews();
    }
}
