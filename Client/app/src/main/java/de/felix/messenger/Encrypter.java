package de.felix.messenger;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Encrypter {

    static File filesDir;

    public Encrypter(File filesDir) {
        Encrypter.filesDir = new File(filesDir, "Keys");
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
        Log.i("Encrypter", "Successfully created a Keypair.");
        return keyPair;
    }

    public static void storeKey(Key key, String fileName){
        try {
            File keyFile = new File(filesDir, fileName);
            keyFile.getParentFile().mkdirs();
            FileOutputStream publicKeyFile = new FileOutputStream(keyFile);
            publicKeyFile.write(key.getEncoded());
            publicKeyFile.close();
            Log.i("Encrypter", "Stored Key on the device");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKey readPublicKeyFromFile(String fileName) {
        try {
            FileInputStream publicKeyFile = new FileInputStream(new File(filesDir, fileName));
            byte[] pubKeyBytes = publicKeyFile.readAllBytes();
            publicKeyFile.close();
            Log.i("Encrypter", "Found a PubKey on the device");

            return createPublicKeyFromBytes(pubKeyBytes);

        } catch (IOException e) {
            Log.i("Encrypter", "No Public Key File found: "+e.toString());
            return null;
        }
    }

    public static PublicKey createPublicKeyFromBytes(byte[] pubKeyBytes){
        KeyFactory pubKeyFactory = null;
        try {
            pubKeyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyBytes);
            PublicKey publicKey = pubKeyFactory.generatePublic(pubKeySpec);
            return publicKey;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

            Log.i("Encrypter", "Failed to creating pubkey from bytes"+e.toString());
            return null;
        }
    }

    public static PrivateKey readPrivateKeyFromFile(String fileName){
        try {
            File publicKeyFile = new File(filesDir, fileName);
            byte[] privKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

            KeyFactory privKeyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privKeyBytes);

            Log.i("Encrypter", "Found a PrivateKey on the device");
            return privKeyFactory.generatePrivate(privKeySpec);

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.i("Encrypter", "No Private Key File found: "+e.toString());
            return null;
        }
    }


    public static byte[] encryptString(String stringToEncrypt, PublicKey givenPublicKey) {
        try {
            Log.i("Enrcrypter", "Encrypting String...");
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, givenPublicKey);

            byte[] bytesToEncrypt = stringToEncrypt.getBytes(StandardCharsets.UTF_8);
            return  encryptCipher.doFinal(bytesToEncrypt);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptBytes(byte[] encryptedBytes, PrivateKey privateKey) {
        try {
            Log.i("Enrcrypter", "Decrypting Bytes...");
            Cipher decryptCypher = Cipher.getInstance("RSA");
            decryptCypher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedBytes = decryptCypher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}