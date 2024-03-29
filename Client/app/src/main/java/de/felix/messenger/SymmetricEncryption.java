package de.felix.messenger;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import javax.crypto.spec.SecretKeySpec;


public class SymmetricEncryption {

    static File baseDir;
    static String symKeyFileName;
    static String symIVFileName;
    static String symHashFileName;

    public SymmetricEncryption(File baseDir)  {
        SymmetricEncryption.baseDir = new File(baseDir, "Keys");
        symKeyFileName = "SymmetricKey.key";
        symIVFileName = "SymmetricIV.key";
        symHashFileName = "SymmetricHash.key";
    }


    /**
     * Erstellt einen symmetrischen Schlüssel
     */
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

    /**
     * Erstellt für den symmetrischen Schlüssel einen Initialisation-Vector (IV)
     */
    public static IvParameterSpec generateIV(){
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * Hasht den symmetrischen schlüssel, um ihn für andere zuordenbar zu machen. Der Hash ist
     * eindeutig und kann als identifikation verwendet werden. Der Hash wird immer mit dem Schlüssel
     * mitgeschickt, um beim verschlüsseln zu überprüfen, ob der richtige schlüssel verwendet wird.
     */
    public static String generateKeyHash(SecretKey secretKey){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(secretKey.getEncoded());

            return Base64.getEncoder().encodeToString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Erstellt einen symmetrischen schlüssel von rohen Bytes
     */
    public static SecretKey createSymKeyFromBytes(byte[] symKeyBytes) {
        return new SecretKeySpec(symKeyBytes,"AES");
    }

    /**
     * Verschlüsselt einen String mit den symmetrischen Schlüssel
     */
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

    /**
     * Entschlüsselt gegebene Bytes mit den symmetrischen Schlüssel
     */
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

    /**
     *   speichert den symmetrischen Schlüssel in einer Datei
     */
    public static void saveSymKeyInFile(SecretKey symKey, IvParameterSpec iv, String symKeyHash) {
        try {
            FileOutputStream symKeyFile = new FileOutputStream(new File(baseDir, symKeyFileName));
            symKeyFile.write(symKey.getEncoded());
            symKeyFile.close();

            FileOutputStream ivFile = new FileOutputStream(new File(baseDir, symIVFileName));
            ivFile.write(iv.getIV());
            ivFile.close();

            FileOutputStream symHashFile = new FileOutputStream(new File(baseDir, symHashFileName));
            symHashFile.write(symKeyHash.getBytes(StandardCharsets.UTF_8));
            symHashFile.close();
            Log.i("SymmetricEncryption", "Stored SymKeys on the device");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Erstellt den symmetrischen Schlüssel aus den Dateien, falls eine vorhanden ist
     */
    public static SecretKey loadSymKeyFromFile() {
        try {
            FileInputStream symKeyFile = new FileInputStream(new File(baseDir, symKeyFileName));
            byte[] symKeyBytes = symKeyFile.readAllBytes();
            SecretKey symKey = createSymKeyFromBytes(symKeyBytes);
            symKeyFile.close();

            Log.i("SymmetricEncryption", "Loaded SymKey on the device");
            return symKey;


        } catch (IOException e) {
            Log.w("SymmetricEncription", "No symkey found on the device "+e.toString());
            return null;
        }
    }

    /**
     * lädt den IV aus der Datei, falls vorhanden
     */
    public static IvParameterSpec loadIVFromFile() {
        try {

            FileInputStream ivFile = new FileInputStream(new File(baseDir, symIVFileName));
            byte[] symIVBytes = ivFile.readAllBytes();
            IvParameterSpec iv = new IvParameterSpec(symIVBytes);
            ivFile.close();

            Log.i("SymmetricEncryption", "Loaded SymKeys on the device");
            return iv;

        } catch (IOException e) {
            Log.w("SymmetricEncription", "No iv found on the device "+e.toString());
            return null;
        }
    }

    /**
     * Lädt den Schlüssel Hash aus der Datei, falls vorhanden
     */
    public static String loadSymHashFromFile() {
        try {
            FileInputStream symHashFile = new FileInputStream(new File(baseDir, symHashFileName));
            String symHash = new String(symHashFile.readAllBytes());
            symHashFile.close();

            Log.i("SymmetricEncryption", "Loaded SymKeys on the device");
            return symHash;

        } catch (IOException e) {
            Log.w("SymmetricEncription", "No symhash found on the device "+e.toString());
            return null;
        }
    }

}
