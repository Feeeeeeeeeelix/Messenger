package de.felix.messenger;

import android.content.Context;
import android.os.Build;

import java.security.PublicKey;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class Message {

    String senderName;
    String timeCreatedDisplay;
    Instant timeCreated;

    String textContent;
    int side;

    public Message(String textContent, String senderName, int Side){
        this.side = Side;
        this.textContent = textContent;
        this.senderName = senderName;
        saveTime();
    }

    /**
     * Erstelle eine Nachricht-Instanz mit einer gegebenen Erstellzeit
     */
    public Message(String textContent, String senderName, int Side, long givenCreationTime){
        this.side = Side;
        this.textContent = textContent;
        this.senderName = senderName;

        timeCreated = Instant.ofEpochMilli(givenCreationTime);
        timeCreatedDisplay = DateTimeFormatter.ofPattern("HH:mm").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault()).format(timeCreated);
    }

    /**
     * Sichert die aktuelle Zeit, an der diese Nachricht erstellt wurde
     */
    public void saveTime(){
        timeCreated = Instant.now();
        timeCreatedDisplay = DateTimeFormatter.ofPattern("HH:mm").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault()).format(timeCreated);
    }

    /**
     * Erstellt ein Layout und gibt ihn zurück
     */
    public MessageLayout getLayout(Context context){
        MessageLayout layout = new MessageLayout(context,textContent, senderName, side);
        layout.setTimeStamp(timeCreatedDisplay);
        return layout;
    }


    /**
     * Verschlüssel den Text-Inhalt
     */
    public byte[] getEncrypted(PublicKey publicKey){
        return Encrypter.encryptString(textContent, publicKey);
    }


    /**
     * Gibt die Erstellzeit der Nachricht in ms seit Epoch zurück
     */
    public long getCreationTime(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return timeCreated.toEpochMilli();
        }
        return 0;
    }
}


