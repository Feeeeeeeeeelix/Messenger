package de.felix.messenger;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Communication {

    Mqtt5AsyncClient client;
    final String TOPIC;
    final Chat chatReference;
    final String clientName;

    final String JsonKeyTypeMessage = "Message";
    final String JsonKeyContent = "MessageContent";
    final String JsonKeySymKeyHash = "SymKeyHash";
    final String JsonKeyTime = "TimeCreated";
    final String JsonKeyName = "Name";
    final String JsonKeyTypeRequest = "RequestSymmetricKey";
    final String JsonKeyPubKey = "PubKey";
    final String JsonKeyTypeSymKey = "SymKeyPost";
    final String JsonKeySymKey = "SymKey";
    final String JsonKeySymIV = "SymIV";

    public Communication(Context context, String mqttTopic, Chat chatReference, String clientName) {
        String HOST = "open-messenger-application.duckdns.org";
        int PORT = 123;
        TOPIC = mqttTopic;
        String USERNAME = "Pablo";
        String PASSWORD = "ETS2isfun";

        this.chatReference = chatReference;
        this.clientName = clientName;

        boolean new_session;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String CLIENT_ID = sharedPreferences.getString("UUID", null);

        if (CLIENT_ID == null) {
            CLIENT_ID = UUID.randomUUID().toString();

            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("UUID", CLIENT_ID);
            myEdit.apply();

            new_session = true;
        } else {
            new_session = false;
        }


        client = Mqtt5Client.builder()
                .identifier(CLIENT_ID)
                .serverHost(HOST)
                .serverPort(PORT)
                .automaticReconnect()
                .initialDelay(500, TimeUnit.MILLISECONDS)
                .maxDelay(15, TimeUnit.SECONDS)
                .applyAutomaticReconnect()
                .buildAsync();

        client.connectWith()
                .cleanStart(true) // new_session
                .sessionExpiryInterval(2628000)
                .simpleAuth()
                .username(USERNAME)
                .password(PASSWORD.getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        Log.e("Communication", "Connection unsuccessful");
                    } else {
                        // setup subscribes
                        client.subscribeWith()
                                .topicFilter(TOPIC)
                                .qos(MqttQos.EXACTLY_ONCE)
                                .callback(publish -> {
                                    onReceiveMessage(publish.getPayloadAsBytes());
                                        }
                                )
                                .send()
                                .whenComplete((subAck, throwable2) -> {
                                    if (throwable2 != null) {
                                        Log.e("Communication","Subscribing unsuccessful: " + subAck + ",, " + throwable2);
                                    } else {
                                        // Handle successful subscription, e.g. logging or incrementing a metric
                                        Log.d("Communication", "Subscribing successful: " + subAck);
                                    }
                                });
                    }
                });
    }

    public void disconnect(){
        client.disconnect();
    }

    public void onReceiveMessage(byte[] byteContent) {
        Log.i("Communication", String.format("Received Message : %s", byteContent));

        try {
            JSONObject obj = new JSONObject(new String(byteContent));
            Log.d("Communication", String.format("Received json : %s", obj.toString()));

            if (obj.has(JsonKeyTypeMessage)){
                JSONObject messageContent = obj.getJSONObject(JsonKeyTypeMessage);

                String messageText = messageContent.getString(JsonKeyContent);
                Long timeCreated = Long.parseLong(messageContent.getString(JsonKeyTime));
                String messageSender  = messageContent.getString(JsonKeyName);
                String symKeyHash = messageContent.getString(JsonKeySymKeyHash);

                chatReference.onReceiveMessage(messageText, timeCreated, messageSender, symKeyHash);

            }
            else if (obj.has(JsonKeyTypeRequest)) {
                JSONObject requestContent = obj.getJSONObject(JsonKeyTypeRequest);
                String receiverPubKey = requestContent.getString(JsonKeyPubKey);
                String receiverName = requestContent.getString(JsonKeyName);

                chatReference.sendSymmetricKey(receiverPubKey, receiverName);

            }
            else if (obj.has(JsonKeyTypeSymKey)) {
                JSONObject symKeyContent = obj.getJSONObject(JsonKeyTypeSymKey);
                String receiverName = symKeyContent.getString(JsonKeyName);

                String symKey = symKeyContent.getString(JsonKeySymKey);
                String symIVString = symKeyContent.getString(JsonKeySymIV);
                String symKeyHash = symKeyContent.getString(JsonKeySymKeyHash);

                if (receiverName.equals(clientName)){
                    chatReference.keyManager.saveSymmetricKey(symKey, symIVString, symKeyHash);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
        /*Sort
         * message
         *   text (encrpyted with symkey)
         *   time
         *   sender
         *   keyhash
         *
         * symkeyrequest
         *   pubkey
         *   name of sender
         *
         * symkey
         *   symkey (encrypted with giben pubkey)
         *   name of receiver
         *
         * */

    public void SendMessage(Message messageToSend, SecretKey symmetricKey, IvParameterSpec iv, String SymKeyHash){
        Log.i("Communication", "Sending Message...");

        byte[] encryptedBytes = SymmetricEncryption.encryptStringSymmetric(messageToSend.textContent, symmetricKey, iv);
        String base64EncodedMessageContent = Base64.getEncoder().encodeToString(encryptedBytes);

        JSONObject objToSend = new JSONObject();

        try {
            JSONObject messageContent = new JSONObject();

            messageContent.put(JsonKeyContent, base64EncodedMessageContent);
            messageContent.put(JsonKeyTime, String.valueOf(messageToSend.getCreationTime()));
            messageContent.put(JsonKeyName, clientName);
            messageContent.put(JsonKeySymKeyHash, SymKeyHash);

            objToSend.put(JsonKeyTypeMessage, messageContent);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        byte[] sendData = objToSend.toString().getBytes(StandardCharsets.UTF_8);
        publishMessage(sendData);
    }

    public void publishMessage(byte[] byteContent){
        Log.d("Communication", String.format("Publishing bytes: %s", new String(byteContent)));
        client.publishWith()
                .topic(TOPIC)
                .qos(MqttQos.EXACTLY_ONCE)
                //.retain(false)
                .payload(byteContent)
                .messageExpiryInterval(2628000)
                .send()
                .whenComplete((publish, throwable) -> {
                    if (throwable != null) {
                        Log.e("Communication", "Publication failed: "+throwable);
                    } else {
                        Log.d("Communication", "Publication successful");
                    }
                });
    }

    public void requestSymmetricKey(String publicKeyString){
        Log.d("Communication", "Requesting SymKey");
        JSONObject obj = new JSONObject();
        try {
            JSONObject requestContent = new JSONObject();
            requestContent.put(JsonKeyName, clientName);
            requestContent.put(JsonKeyPubKey, publicKeyString);

            obj.put(JsonKeyTypeRequest, requestContent);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        byte[] sendData = obj.toString().getBytes(StandardCharsets.UTF_8);
        publishMessage(sendData);
    }


    public void sendSymmetricKey(String symKeyString,String symIVString,String symKeyHash, String receiverName){
        Log.d("Communication", "Sending SymKey");
        JSONObject obj = new JSONObject();
        try {
            JSONObject symKeyContent = new JSONObject();
            symKeyContent.put(JsonKeyName, receiverName);

            symKeyContent.put(JsonKeySymKey, symKeyString);
            symKeyContent.put(JsonKeySymIV, symIVString);
            symKeyContent.put(JsonKeySymKeyHash, symKeyHash);

            obj.put(JsonKeyTypeSymKey, symKeyContent);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        byte[] sendData = obj.toString().getBytes(StandardCharsets.UTF_8);
        publishMessage(sendData);
    }
}
