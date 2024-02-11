package de.felix.messenger;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Encrypter {


    KeyPair keyPair;

    PrivateKey privateKey;
    PublicKey publicKey;

    public void generateKeyPair() throws NoSuchAlgorithmException {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public void storeKeyPair(){

    }

    public byte[] encryptString(String stringToEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {


        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] bytesToEncrypt = stringToEncrypt.getBytes(StandardCharsets.UTF_8);

        byte[] encryptedBytes = encryptCipher.doFinal(bytesToEncrypt);

        return encryptedBytes;
    }

    public String decryptString(byte[] encryptedBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher decryptCypher = Cipher.getInstance("RSA");
        decryptCypher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptedBytes = decryptCypher.doFinal(encryptedBytes);

        String decryptedString = new String(decryptedBytes, StandardCharsets.UTF_8);

        return decryptedString;
    }
}
