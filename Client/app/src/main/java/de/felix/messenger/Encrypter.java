package de.felix.messenger;

import android.util.Log;

import java.security.KeyPair;
import java.security.PublicKey;

public class Encrypter {

    public String toString() {
        return "Encrypter{" +
                "keyPair=" + keyPair +
                '}';
    }

    String keyPair;

    public void encrypt(String message){
        Log.i("encrypt", message);
        Log.i("instance", this.toString());
        if (keyPair == null) {
            createKeyPair();
        }


    }
    public void createKeyPair(){
        Log.i("creatingkeypair", "creating");
        keyPair = "e";
    }
}
