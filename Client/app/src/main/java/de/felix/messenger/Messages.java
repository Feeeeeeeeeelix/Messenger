package de.felix.messenger;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Comparator;

public class Messages {

    String chatKey;
    File baseDir;
    File filesFolderSend;
    File filesFolderReceived;
    KeyManager keyManager;

    public Messages(File baseDir, String chatKey, KeyManager keyManager) {
        this.baseDir = new File(baseDir, String.format("Chat-%s/", chatKey));
        this.filesFolderSend = new File(baseDir, String.format("Chat-%s/Send/", chatKey));
        this.filesFolderReceived = new File(baseDir, String.format("Chat-%s/Received/", chatKey));
        this.chatKey = chatKey;
        this.keyManager = keyManager;
    }

    /**
     * Lade die Nachrichten aus den Dateien. Die gesendeten und empfangenen Nachrichten werden in zwei
     * getrennten Ordnern gespeichert, um sie zuordnen zu können. Der Name der Datei ist die
     * Erstellzeit der Nachricht, in ms seit Epoch, und der inhalt ist der Text-Inhalt, verschlüsselt
     * mit dem eigenen öffentlichen Schlüssel
     */
    public ArrayList<Message> loadMessages(){
        ArrayList<Message> loadedMessages = new ArrayList<Message>();

        try {
            if (filesFolderSend.listFiles() != null){
                for (File messageFile : filesFolderSend.listFiles()){
                    FileInputStream fis = new FileInputStream(messageFile);

                    byte[] messageBytes = fis.readAllBytes();
                    String fileName = messageFile.getName();
                    fis.close();

                    Message newMessage = createMessageFromFileContent(messageBytes, fileName, 1);
                    loadedMessages.add(newMessage);
                    Log.d("Messages", "Loaded send message");
                }
            }

            if (filesFolderReceived.listFiles() != null){
                for (File messageFile : filesFolderReceived.listFiles()){
                    FileInputStream fis = new FileInputStream(messageFile);

                    byte[] messageBytes = fis.readAllBytes();
                    String fileName = messageFile.getName();
                    fis.close();

                    Message newMessage = createMessageFromFileContent(messageBytes, fileName, 0);
                    loadedMessages.add(newMessage);
                    Log.d("Messages", "Loaded received message");
                }
            }

            loadedMessages.sort(Comparator.comparingLong(Message::getCreationTime));
            return loadedMessages;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Erstellt von der rohen Bytes einer Datei ein Nachricht-Objekt
     */
    private Message createMessageFromFileContent(byte[] encryptedMessageBytes, String fileName, int side){
        String messageContent = Encrypter.decryptBytes(encryptedMessageBytes, keyManager.getOwnPrivateKey());
        String senderName = "";
        if (fileName.contains(";")){
            String[] data = fileName.split(";");
            fileName = data[0];
            senderName = data[1];
        }
        Long timeCreated = Long.valueOf(fileName);
        Message newMessage = new Message(messageContent, senderName, side, timeCreated);
        return  newMessage;
    }

    /**
     * Sichert eine neue Nachricht in dem zugehörigen Ordner
     */
    public void saveNewMessage(Message newMessage, PublicKey pubKey){
        Log.i("Messages", "Saving Message on the device...");
        try {
            int side = newMessage.side;
            String fileName = String.valueOf(newMessage.getCreationTime())+";"+newMessage.senderName;
            byte[] encryptedBytes = newMessage.getEncrypted(pubKey);

            File messageFile = new File(side == 1 ? filesFolderSend: filesFolderReceived , fileName);
            messageFile.getParentFile().mkdirs();
            messageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(messageFile);

            fos.write(encryptedBytes);
            fos.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Löscht alle Nachrichten aus den Ordnern
     */
    public void deleteAllMessages(){
        deleteDirectory(baseDir);
    }

    private void deleteDirectory(File directoryToDelete){
        if (directoryToDelete.listFiles() != null){
            for (File subfile: directoryToDelete.listFiles()){
                if (subfile.isDirectory()) {
                    deleteDirectory(subfile);
                }
                subfile.delete();
            }
        }
        directoryToDelete.delete();
    }
}
