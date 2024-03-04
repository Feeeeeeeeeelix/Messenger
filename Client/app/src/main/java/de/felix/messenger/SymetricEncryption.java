package de.felix.messenger;

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

public class SymetricEncryption {
    public SymetricEncryption() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String inputString = "test";

//        SecretKey key = generateKey();
//        IvParameterSpec iv = generateIV();
//        byte[] encryptedBytes = encryptString(inputString, key, iv);
//        String decryptedString = decryptBytes(encryptedBytes, key, iv);
//
//        assert inputString.equals(decryptedString);



        KeyPair keypar = generateKeyPair();
        byte[] encryptedBytes = encryptString(inputString, keypar.getPublic());
        String decryptedString = decryptBytes(encryptedBytes, keypar.getPrivate());
        assert  inputString.equals(decryptedString);


    }

    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    private static IvParameterSpec generateIV(){
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static byte[] encryptString(String input, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(input.getBytes());
    }
    public static String decryptBytes(byte[] cipherText, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText);
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
}
