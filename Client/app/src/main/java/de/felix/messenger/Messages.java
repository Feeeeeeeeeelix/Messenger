package de.felix.messenger;

import android.os.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public ArrayList<Message> loadMessages(){
        ArrayList<Message> loadedMessages = new ArrayList<Message>();

        try {
            if (filesFolderSend.listFiles() != null){
                for (File messageFile : filesFolderSend.listFiles()){
                    FileInputStream fis = new FileInputStream(messageFile);

                    byte[] messageBytes = fis.readAllBytes();
                    String fileName = messageFile.getName();

                    Message newMessage = createMessageFromFileContent(messageBytes, fileName, 1);
                    loadedMessages.add(newMessage);
                }
            }

            if (filesFolderReceived.listFiles() != null){
                for (File messageFile : filesFolderReceived.listFiles()){
                    FileInputStream fis = new FileInputStream(messageFile);

                    byte[] messageBytes = fis.readAllBytes();
                    String fileName = messageFile.getName();

                    Message newMessage = createMessageFromFileContent(messageBytes, fileName, 0);
                    loadedMessages.add(newMessage);
                }
            }

            loadedMessages.sort(Comparator.comparingLong(Message::getCreationTime));
            return loadedMessages;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Message createMessageFromFileContent(byte[] encryptedMessageBytes, String fileName, int side){
        String messageContent = Encrypter.decryptString(encryptedMessageBytes, keyManager.getOwnPrivateKey());
        Long timeCreated = Long.valueOf(fileName);
        Message newMessage = new Message(side, timeCreated);
        newMessage.setText(messageContent);
        return  newMessage;
    }

    public void saveNewMessage(byte[] messageText, long messageTime, int side){
        try {
            String fileName = String.valueOf(messageTime);
            File messageFile = new File(side == 1 ? filesFolderSend: filesFolderReceived , fileName);
            messageFile.getParentFile().mkdirs();
            messageFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(messageFile);

            fos.write(messageText);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
