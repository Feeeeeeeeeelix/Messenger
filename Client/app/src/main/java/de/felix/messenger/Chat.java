package de.felix.messenger;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    ConstraintLayout mainChatFrame;
    LinearLayout mainChatLayout;
    Button sendMessageButton;
    EditText messageEntry;

    List<Message> messages;
    Context context;

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
                createMessage();
            }
        });
    }

    public void receiveMessage(){

    }

    public void createMessage(){
//        Get Message Text and create Message Object
        String currentMessage = messageEntry.getText().toString();
        Message newMessage = new Message(context);
        newMessage.setText(currentMessage);

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

    }

    public void updateTimeStamps(){
//    TODO: update Timestamps
    }
}
