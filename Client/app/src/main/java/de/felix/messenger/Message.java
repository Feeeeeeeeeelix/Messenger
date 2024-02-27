package de.felix.messenger;

import android.content.Context;
import android.os.Build;

import java.security.PublicKey;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class Message {

    String timeCreatedDisplay;
    Instant timeCreated;

    Context context;
    String textContent;
    int side;

    public Message(Context context, int Side){
        this.context = context;
        this.side = Side;
        saveTime();
    }

    public Message(Context context, int Side, long givenCreationTime){
        this.context = context;
        this.side = Side;

        timeCreated = Instant.ofEpochSecond(givenCreationTime);
        timeCreatedDisplay = DateTimeFormatter.ofPattern("HH:mm").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault()).format(timeCreated);
    }

    public void saveTime(){
        timeCreated = Instant.now();
        timeCreatedDisplay = DateTimeFormatter.ofPattern("HH:mm").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault()).format(timeCreated);
    }

    public MessageLayout getLayout(){
        MessageLayout layout = new MessageLayout(context, side);
        layout.setMessageContent(textContent);
        layout.setTimeStamp(timeCreatedDisplay);
        return layout;
    }

    public void setText(String textMessage){
        textContent = textMessage;

    }


    public byte[] getEncrypted(PublicKey publicKey){
        return Encrypter.encryptString(textContent, publicKey);
    }

    public long getCreationTime(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return timeCreated.getEpochSecond();
        }
        return 0;
    }
}


