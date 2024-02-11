package de.felix.messenger;

import android.content.Context;
import android.media.Image;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class Message {

    MessageLayout messageLayout;

    String timeCreatedDisplay;
    LocalDateTime timeCreatedLong;

    Context context;
    String textContent;
    Image imageContent;

    public Message(Context context){
        this.context = context;
        saveTime();
    }

    public void saveTime(){
//        TODO: find non critical way to save time
        LocalTime timeCreatedShort = LocalTime.now();
        timeCreatedLong = LocalDateTime.now();
        timeCreatedDisplay = timeCreatedShort.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public MessageLayout getLayout(){
        MessageLayout layout = new MessageLayout(context);
        layout.setMessageContent(textContent);
        layout.setTimeStamp(timeCreatedDisplay);
        return layout;
    }

    public void setText(String textMessage){
        textContent = textMessage;

    }

    public void setImage(Image image){
        imageContent = image;
    }
}


