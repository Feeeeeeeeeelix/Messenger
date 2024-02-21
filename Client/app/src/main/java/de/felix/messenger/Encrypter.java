package de.felix.messenger;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Encrypter {


    KeyPair keyPair;

    private PrivateKey privateKey = null;
    PublicKey publicKey = null;

    static File filesDir;

    public Encrypter(File filesDir) {
        Encrypter.filesDir = filesDir;
    }

    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException {
        if (privateKey == null){

            PublicKey pubKey = readPublicKeyFromFile("d");
            if (pubKey == null){
                generateKeyPair();
            }
        }

        return privateKey;
    }

    private void generateKeyPair() throws NoSuchAlgorithmException {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public static void storePublicKey(PublicKey pubKey, String fileName){
        try {
            FileOutputStream publicKeyFile = new FileOutputStream(new File(filesDir, fileName));
            publicKeyFile.write(pubKey.getEncoded());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKey readPublicKeyFromFile(String fileName) {
        try {
            FileInputStream publicKeyFile = new FileInputStream(new File(filesDir, fileName));
            byte[] pubKeyBytes = publicKeyFile.readAllBytes();

            KeyFactory pubKeyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyBytes);
            PublicKey publicKey = pubKeyFactory.generatePublic(pubKeySpec);

            return publicKey;

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.i("Encrypter", "No Public Key File found: "+e.toString());
            return null;
        }
    }

    public void readKeyPairFromFiles(){
        try {
            FileInputStream publicKeyFile = new FileInputStream(new File(filesDir, "public.key"));
            byte[] pubKeyBytes = publicKeyFile.readAllBytes();
            KeyFactory pubKeyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyBytes);
            publicKey = pubKeyFactory.generatePublic(pubKeySpec);

            FileInputStream privateKeyFile = new FileInputStream(new File(filesDir, "private.key"));
            byte[] privKeyBytes = privateKeyFile.readAllBytes();
            KeyFactory privKeyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privKeySpec = new X509EncodedKeySpec(privKeyBytes);
            privateKey = privKeyFactory.generatePrivate(privKeySpec);

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encryptString(String stringToEncrypt, PublicKey givenPublicKey) {
        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, givenPublicKey);

            byte[] bytesToEncrypt = stringToEncrypt.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedBytes = encryptCipher.doFinal(bytesToEncrypt);

            return encryptedBytes;

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public String decryptString(byte[] encryptedBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher decryptCypher = Cipher.getInstance("RSA");
        decryptCypher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptedBytes = decryptCypher.doFinal(encryptedBytes);

        String decryptedString = new String(decryptedBytes, StandardCharsets.UTF_8);

        return decryptedString;
    }
}
