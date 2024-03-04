package de.felix.messenger;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyManager {

    private PublicKey publicKeyPartner;
    private PublicKey ownPublicKey;
    private PrivateKey ownPrivateKey;

    String chatIdentifier;

    public KeyManager(String chatIdentifier) {
        this.chatIdentifier = chatIdentifier;

        PublicKey p = getOwnPublicKey();
        PublicKey p1 = getOwnPublicKey();

        assert p.equals(p1);
    }

    public PublicKey getPublicKeyPartner() {
//        Get the pub key of the other person (the receiver of the message that will be encrypted)
        if (publicKeyPartner != null){
            return publicKeyPartner;
        }

//          Load Key from file or request it if it does not exist
        String publicKeyName = chatIdentifier + "_public.key";
        PublicKey pubKey = Encrypter.readPublicKeyFromFile(publicKeyName);
        if (pubKey != null){
            return pubKey;
        }

//        The key does not exist. Request ist from the partner
        pubKey = requestPublicKeyPartner();
        Encrypter.storeKey(pubKey, publicKeyName);

        return pubKey;
    }

    private PublicKey requestPublicKeyPartner(){
//        TODO: request public key from partner
        return publicKeyPartner;
    }

    public PublicKey getOwnPublicKey(){
//        if (ownPublicKey != null){
//            return ownPublicKey;
//        }

//        Load the public key from the device
        String publicKeyName =   "own_public.key";
        PublicKey pubKey = Encrypter.readPublicKeyFromFile(publicKeyName);
        if (pubKey == null){

//            The key does not exist. Create one
            createOwnKeyPair();
            pubKey = this.ownPublicKey;
        }
        return pubKey;
    }

    public PrivateKey getOwnPrivateKey(){
        if (ownPrivateKey != null){
            return ownPrivateKey;
        }

//        Load the private key from the device
        String privateKeyName =   "own_private.key";
        PrivateKey privateKey = Encrypter.readPrivateKeyFromFile(privateKeyName);
        if (privateKey == null){

//            The key does not exist. Create one
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
