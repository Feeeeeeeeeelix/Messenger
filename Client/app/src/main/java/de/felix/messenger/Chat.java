package de.felix.messenger;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;


import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;


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

    List<Message> messagesToBeSend;

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

        client = new Communication(context, "chat1", this, clientName);
        keyManager = new KeyManager(clientName, client);

        messages = new Messages(context.getFilesDir(), clientName, keyManager);

        loadMessages();
        messagesToBeSend = new ArrayList<>();

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

    private void sendMessage(Message messageToSend){
        if (keyManager.symmetricKey == null){
            Log.i("Chat", "Didnt habe a symkey, requesting one");
            client.requestSymmetricKey(Base64.getEncoder().encodeToString(keyManager.getOwnPublicKey().getEncoded()));

            messagesToBeSend.add(messageToSend);

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    checkSymKeyAndSendMessages();
                }
            };

            Timer timer = new Timer();
            int delay = 1000;
            timer.schedule(task, delay);

        }else {
            client.SendMessage(messageToSend, keyManager.symmetricKey, keyManager.iv, keyManager.symKeyHash);
        }
    }

    private void checkSymKeyAndSendMessages(){
        Log.i("Chat", "Check if i have a symkey");

        if (keyManager.symmetricKey == null){
//            I am the first one to send: create a sym key and send my messages
            Log.i("Chat", "i didnt receive any symkey, i will create one");
            keyManager.createSymmetricKey();
        }

        if (messagesToBeSend.size() > 0){
            try {
                for (Message cachedMessage: messagesToBeSend){
                    sendMessage(cachedMessage);
                    messagesToBeSend.remove(cachedMessage);
                }
            }catch (ConcurrentModificationException e){
                Log.i("Chat", "some went wrong while deleting message to be send. messages: ");
                Log.i("Chat", messagesToBeSend.toString());

            }
        }

    }

    public void onReceiveMessage(String encodedText, Long timeCreated, String senderName, String symKeyHash){
        Log.i("Chat", "Received Message");
        if (!symKeyHash.equals(keyManager.symKeyHash)){
            client.requestSymmetricKey(Base64.getEncoder().encodeToString(keyManager.getOwnPrivateKey().getEncoded()));
            return;
        }

//        if (senderName.equals(clientName)) return;TODO dont show own messages

        byte[] encryptedBytes = Base64.getDecoder().decode(encodedText);
        String messageText = SymmetricEncryption.decryptBytesSymmetric(encryptedBytes, keyManager.symmetricKey, keyManager.iv);

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

    private void loadMessages(){
        Log.i("Chat", "loading Messages...");

//        Load all the message from the device and show them on the screen
        ArrayList<Message> Messages = messages.loadMessages();

        for (Message newMessage: Messages) {

//        Place the message on the screen
            placeNewMessage(newMessage);
        }

    }

    private void placeNewMessage(Message message){
        Log.i("Chat", "Placing new Message");
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

    private void deleteChatContent(){
        Log.i("Chat", "Deleting Chat Content");

        messages.deleteAllMessages();
        mainChatLayout.removeAllViews();
    }
}
