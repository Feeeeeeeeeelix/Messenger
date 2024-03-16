package de.felix.messenger;

import android.util.Log;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class KeyManager {

    private PublicKey ownPublicKey;
    private PrivateKey ownPrivateKey;

    public SecretKey symmetricKey;
    public IvParameterSpec iv;
    public String symKeyHash;

    final String chatIdentifier;
    Communication client;


    /**
     * Der Keymanager speichert und verwaltet die symmetrischen und asymmetrischen Schlüssel vom client.
     */
    public KeyManager(String chatIdentifier, Communication client) {
        this.chatIdentifier = chatIdentifier;
        this.client = client;

        loadSymKeysFromFile();
    }

    /**
     * Gebe den eigenen öffentlichen Schlüssel zurück. Wenn keiner existiert, erstelle einen
     */
    public PublicKey getOwnPublicKey(){
        Log.d("KeyManager", "Requested own public key");
        if (ownPublicKey != null){
            return ownPublicKey;
        }

//        Load the public key from the device
        String publicKeyName =   "own_public.key";
        PublicKey pubKey = Encrypter.readPublicKeyFromFile(publicKeyName);
        if (pubKey == null){
            Log.i("KeyManager", "Didn't found a public Key on the device");

//            The key does not exist. Create one
            createOwnKeyPair();
            pubKey = this.ownPublicKey;
        }
        ownPublicKey = pubKey;
        return pubKey;
    }

    /**
     * Gebe den eigenen privaten Schlüssel zurück. Wenn keiner existiert, erstelle einen
     */
    public PrivateKey getOwnPrivateKey(){
        Log.d("KeyManager", "Requested private key");
        if (ownPrivateKey != null){
            Log.d("KeyManager", "Returnded private key from cache");
            return ownPrivateKey;
        }

//        Load the private key from the device
        String privateKeyName =   "own_private.key";
        PrivateKey privateKey = Encrypter.readPrivateKeyFromFile(privateKeyName);
        if (privateKey == null){
            Log.i("KeyManager", "Didn't found a Private Key on the device");
//            The key does not exist. Create one
            createOwnKeyPair();
            privateKey = this.ownPrivateKey;
        }
        ownPrivateKey = privateKey;
        return privateKey;
    }

    /**
     * Erstellt und speichert einen asymmetischen Schlüsselbund in einer Datei
     */
    private void createOwnKeyPair() {
        Log.i("KeyManager", "Creating KeyPair");
        KeyPair keyPair = Encrypter.generateKeyPair();
        ownPrivateKey = keyPair.getPrivate();
        ownPublicKey = keyPair.getPublic();

        Encrypter.storeKey(ownPrivateKey, "own_private.key");
        Encrypter.storeKey(ownPublicKey, "own_public.key");
    }

    /**
     * Erstellt einen symmetrischen Schlüssel und speichert es in einer datei
     */
    public void createSymmetricKey(){
        Log.d("KeyManager", "Generation Sym Key");
        this.symmetricKey = SymmetricEncryption.generateKey();
        this.iv = SymmetricEncryption.generateIV();
        this.symKeyHash = SymmetricEncryption.generateKeyHash(symmetricKey);

        SymmetricEncryption.saveSymKeyInFile(symmetricKey, iv, symKeyHash);
    }


    /**
     * Erstellt einen symmetrischen Schlüssel von verschlüsseln Bytes. Entschlüsselt sie mit dem
     * eigenen öffentlichen schlüssel und speichert sie in einer datei
     */
    public void saveSymmetricKeyFromPartner(String symKeyString, String symIV, String symKeyHash) {
        Log.i("KeyManager", "Saving symkey on the device");

        byte[] symmetricKeyBytes = Base64.getDecoder().decode(symKeyString);
        String encodedSymKeyString = Encrypter.decryptBytes(symmetricKeyBytes, getOwnPrivateKey());
        byte[] encodedSymKeyBytes = Base64.getDecoder().decode(encodedSymKeyString);
        SecretKey symKey = SymmetricEncryption.createSymKeyFromBytes(encodedSymKeyBytes);

        byte[] symIVBytes = Base64.getDecoder().decode(symIV);
        String encodedSymIVString = Encrypter.decryptBytes(symIVBytes, getOwnPrivateKey());
        byte[] encodedSymIVBytes = Base64.getDecoder().decode(encodedSymIVString);
        IvParameterSpec iv = new IvParameterSpec(encodedSymIVBytes);

        SymmetricEncryption.saveSymKeyInFile(symKey, iv, symKeyHash);

        this.symmetricKey = symKey;
        this.iv = iv;
        this.symKeyHash = symKeyHash;
    }

    /**
     * Verschlüsselt den eigenen symmetrischen Schlüssel mit dem gegebenen öffentlichen Schlüssel
     * des Empfängers und sende die rohen Bytes
     */
    public void sendSymmetricKey(String receiverPubKeyString, String receiverName) {
        Log.i("KeyManager", "Sending symKey..");
        if (symmetricKey == null){
//            no sym key
            Log.i("KeyManager", "No SymKey to Send");
            return;
        }
        PublicKey receiverKey = Encrypter.createPublicKeyFromBytes(Base64.getDecoder().decode(receiverPubKeyString));

        SecretKey symKey = symmetricKey;
        String symKeyString = Base64.getEncoder().encodeToString(symKey.getEncoded());
        String symKeyStringEncoded = Base64.getEncoder().encodeToString(Encrypter.encryptString(symKeyString, receiverKey));

        String ivString = Base64.getEncoder().encodeToString(iv.getIV());
        String ivStringEncoded = Base64.getEncoder().encodeToString(Encrypter.encryptString(ivString, receiverKey));

        client.sendSymmetricKey(symKeyStringEncoded, ivStringEncoded, symKeyHash, receiverName);
    }

    /**
     * Liest den symmetrischen Schlüssel aus den Dateine aus und speichere es in der Klasse
     */
    public void loadSymKeysFromFile(){
        Log.i("KeyManager", "Loading symkeys from the device");

        SecretKey symKey = SymmetricEncryption.loadSymKeyFromFile();
        IvParameterSpec iv = SymmetricEncryption.loadIVFromFile();
        String symHash = SymmetricEncryption.loadSymHashFromFile();

        if (symHash != null){
            Log.d("KeyManager", "found symkeys");
            this.symmetricKey = symKey;
            this.iv = iv;
            this.symKeyHash = symHash;
        }

    }
}
