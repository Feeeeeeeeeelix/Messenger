package de.felix.messenger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import javax.crypto.spec.SecretKeySpec;


public class SymmetricEncryption {

    final File baseDir;

    public SymmetricEncryption(File baseDir)  {
        this.baseDir = baseDir;

    }


    public static SecretKey generateKey()  {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            return keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static IvParameterSpec generateIV(){
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String generateKeyHash(SecretKey secretKey){
        return "";
    }


    public static SecretKey createSymKeyFromBytes(byte[] symKeyBytes) {
        return new SecretKeySpec(symKeyBytes,"AES");
    }

    public static byte[] encryptStringSymmetric(String input, SecretKey key, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            return cipher.doFinal(input.getBytes());

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 BadPaddingException | IllegalBlockSizeException |
                 InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptBytesSymmetric(byte[] cipherText, SecretKey key, IvParameterSpec iv){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 BadPaddingException | IllegalBlockSizeException |
                 InvalidAlgorithmParameterException e) {
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

    public static String decryptBytes(byte[] encryptedBytes, PrivateKey privateKey) {
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



    /*public static void saveKey(SecretKey key, File file) throws IOException
    {
        char[] hex = encodeHex(key.getEncoded());
        writeStringToFile(file, String.valueOf(hex));
    }

    public static SecretKey loadKey(File file) throws IOException
    {
        String data = new String(readFileToByteArray(file));
        byte[] encoded;
        try {
            encoded = decodeHex(data.toCharArray());
        } catch (DecoderException e) {
            e.printStackTrace();
            return null;
        }
        return new SecretKeySpec(encoded, "AES");
    }*/
}
