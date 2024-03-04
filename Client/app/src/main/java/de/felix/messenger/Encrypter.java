package de.felix.messenger;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Encrypter {

    static File filesDir;

    public Encrypter(File filesDir) {
        Encrypter.filesDir = filesDir;
    }

    public static void storeKey(Key key, String fileName){
        try {
            FileOutputStream publicKeyFile = new FileOutputStream(new File(filesDir, fileName));
            publicKeyFile.write(key.getEncoded());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static KeyPair generateKeyPair() {

        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        return keyPair;
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

    public static PrivateKey readPrivateKeyFromFile(String fileName){
        try {
            FileInputStream privateKeyFile = new FileInputStream(new File(filesDir, fileName));
            byte[] privKeyBytes = privateKeyFile.readAllBytes();

            KeyFactory privKeyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privKeySpec = new X509EncodedKeySpec(privKeyBytes);
            return privKeyFactory.generatePrivate(privKeySpec);

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.i("Encrypter", "No Private Key File found: "+e.toString());
            return null;
        }
    }


    public static byte[] encryptString(String stringToEncrypt, PublicKey givenPublicKey) {
        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, givenPublicKey);

            byte[] bytesToEncrypt = stringToEncrypt.getBytes(StandardCharsets.UTF_8);
//            TODO: error while encrypting
            byte[] encryptedBytes = encryptCipher.doFinal(bytesToEncrypt);

//            return bytesToEncrypt;
            return encryptedBytes;

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptString(byte[] encryptedBytes, PrivateKey privateKey) {
        try {
            Cipher decryptCypher = null;
            decryptCypher = Cipher.getInstance("RSA");

            decryptCypher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedBytes = decryptCypher.doFinal(encryptedBytes);

//            return new String(encryptedBytes, StandardCharsets.UTF_8);
            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
