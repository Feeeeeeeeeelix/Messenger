package de.felix.messenger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

public class Messages {

    String chatKey;
    File filesFolder;
//    String fileName;


    public Messages(File baseDir, String chatKey) {
        this.filesFolder = new File(baseDir, String.format("Chat-%s//", chatKey));
        this.chatKey = chatKey;
//        this.fileName = String.format("Chat-%s", chatKey);
    }


    public HashMap<Long, byte[]> loadMessages(){
        HashMap<Long, byte[]> messageDict = new HashMap<>();

        try {
            if (filesFolder.listFiles() == null){
                return messageDict;
            }

            for (File messageFile : filesFolder.listFiles()){
                FileInputStream fis = new FileInputStream(messageFile);
                byte[] messageByte = fis.readAllBytes();

                String fileName = messageFile.getName();
                try {
                    long key = Long.valueOf(fileName);
                    messageDict.put(key, messageByte);

                } catch (Exception e) {
                }
            }

            return messageDict;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void saveNewMessage(byte[] messageText, long messageTime){
        try {
            String fileName = String.valueOf(messageTime);
            File messageFile = new File(filesFolder, fileName);
            messageFile.getParentFile().mkdirs();
            messageFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(messageFile);
//            byte[] messagePrefix = ("\n" + messageTime+ "-").getBytes(StandardCharsets.UTF_8);

            fos.write(messageText);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
