package de.felix.messenger;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyManager {

    private PublicKey publicKeyPartner;
    private PublicKey ownPublicKey;
    private PrivateKey ownPrivateKey;

    String chatIdentifier;

    public KeyManager(String chatIdentifier) {
        this.chatIdentifier = chatIdentifier;
    }

    private void savePublicKey(){
//        save the public key of the other person on the device

        String publicKeyName = chatIdentifier + "_public.key";
        Encrypter.storeKey(publicKeyPartner, publicKeyName);
    }

    public PublicKey getPublicKeyPartner() {
//        Get the pub key of the other person (the receiver of the message that will be encrypted)
        if (publicKeyPartner == null){
            publicKeyPartner = loadPublicKeyPartner();
        }
        return publicKeyPartner;
    }

    private PublicKey loadPublicKeyPartner(){
//        Loads the pub key either from the device or request it from the receiver in case we dont have it
        String publicKeyName = chatIdentifier + "_public.key";
        PublicKey pubKey = Encrypter.readPublicKeyFromFile(publicKeyName);
        if (pubKey == null){
            pubKey = requestPublicKeyPartner();

            Encrypter.storeKey(pubKey, publicKeyName);
        }
        return pubKey;
    }

    private PublicKey requestPublicKeyPartner(){
//        TODO: request public key from partner
        return publicKeyPartner;
    }

    public PublicKey getOwnPublicKey(){
        if (ownPublicKey == null){
            ownPublicKey = loadOwnPublicKey();
        }
        return ownPublicKey;
    }

    private PublicKey loadOwnPublicKey(){
        String publicKeyName =   "own_public.key";
        PublicKey pubKey = Encrypter.readPublicKeyFromFile(publicKeyName);
        if (pubKey == null){
            createOwnKeyPair();
            pubKey = this.ownPublicKey;
        }
        return pubKey;
    }

    public PrivateKey getOwnPrivateKey(){
        if (ownPrivateKey == null){
            ownPrivateKey = loadOwnPrivateKey();
        }
        return ownPrivateKey;
    }

    private PrivateKey loadOwnPrivateKey(){
        String privateKeyName =   "own_private.key";
        PrivateKey privateKey = Encrypter.readPrivateKeyFromFile(privateKeyName);
        if (privateKey == null){
            createOwnKeyPair();
            privateKey = this.ownPrivateKey;
        }
        return privateKey;
    }

    private void createOwnKeyPair() {
        KeyPair keyPair = Encrypter.generateKeyPair();
        ownPrivateKey = keyPair.getPrivate();
        ownPublicKey = keyPair.getPublic();

        Encrypter.storeKey(ownPrivateKey, "own_private.key");
        Encrypter.storeKey(ownPublicKey, "own_public.key");
    }

}
