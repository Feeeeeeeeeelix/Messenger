package de.felix.messenger;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Chat {

    ConstraintLayout mainChatFrame;
    LinearLayout mainChatLayout;
    Button sendMessageButton;
    EditText messageEntry;

    List<Message> messages;
    Context context;

    Integer Id;
    private PublicKey publicKeyReceiver;
    private static PrivateKey privateKeySelf;

    public Chat(Context context, ConstraintLayout mainLayout) {

        this.context = context;
        mainChatFrame = mainLayout;
        mainChatLayout = mainLayout.findViewById(R.id.mainMessageLayout);
        sendMessageButton = mainLayout.findViewById(R.id.button);
        messageEntry = mainLayout.findViewById(R.id.editTextText);

        messages = new ArrayList<Message>();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOwnMessage();
            }
        });
    }

    private void savePublicKey(PublicKey publicKey){
        publicKeyReceiver = publicKey;
        String publicKeyName = Id.toString() + "_public.key";
        Encrypter.storePublicKey(publicKey, publicKeyName);
    }

    public PublicKey getPublicKeyReceiver() {
        if (publicKeyReceiver == null){
            publicKeyReceiver = getPublicKeyFromFiles();
        }
        return publicKeyReceiver;
    }

    private PublicKey getPublicKeyFromFiles(){
        String publicKeyName = Id.toString() + "_public.key";
        PublicKey pubKey = Encrypter.readPublicKeyFromFile(publicKeyName);
        if (pubKey == null){
            pubKey = requestPublicKey();
        }
        return pubKey;
    }

    private PublicKey requestPublicKey(){
        return publicKeyReceiver;
    }

    public void createOwnMessage(){
//        Get Message Text and create Message Object
        String currentMessage = messageEntry.getText().toString();
        Message newMessage = new Message(context, currentMessage.length()%2);
        newMessage.setText(currentMessage);
//        newMessage.setText(currentMessage);

//        Place the message on the screen
        placeNewMessage(newMessage);

        messages.add(newMessage);

        sendMessage(newMessage);

    }

    public void placeNewMessage(Message message){
//        Create a Layout for the message and place it in the main layout
        MessageLayout messageLayout = message.getLayout();
        mainChatLayout.addView(messageLayout);
    }

    public void sendMessage(Message messageToSend){
//        byte[] encryptedBytesToSend = Encrypter.encryptString(messageToSend.textContent, publicKeyReceiver);

        //send..
    }

    public void receiveMessage(){

    }

    private String decryptStringMessage(byte[] encryptedBytes){
        return new String();
    }

    public void updateTimeStamps(){
//    TODO: update Timestamps
    }
}
