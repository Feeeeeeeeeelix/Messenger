package de.felix.messenger;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Communication {

    Mqtt5AsyncClient client;
    String TOPIC;
    Chat chatReference;

    public Communication(Context context, String mqttTopic, Chat chatReference) {
        String HOST = "open-messenger-application.duckdns.org";
        Integer PORT = 123;
        TOPIC = mqttTopic;
        String USERNAME = "Pablo";
        String PASSWORD = "ETS2isfun";

        this.chatReference = chatReference;

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
                .cleanStart(new_session) // new_session
                .sessionExpiryInterval(2628000)
                .simpleAuth()
                .username(USERNAME)
                .password(PASSWORD.getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        log("Connection unsuccessful");
                    } else {
                        // setup subscribes
                        client.subscribeWith()
                                .topicFilter(TOPIC)
                                .qos(MqttQos.EXACTLY_ONCE)
                                .callback(publish -> {
                                    onReceiveMessage(new String(publish.getPayloadAsBytes()));
                                        }
                                )
                                .send()
                                .whenComplete((subAck, throwable2) -> {
                                    if (throwable2 != null) {
                                        log("Subscribing unsuccessful: " + subAck + ",, " + throwable2);
                                    } else {
                                        // Handle successful subscription, e.g. logging or incrementing a metric
                                        log("Subscribing successful: " + subAck);
                                    }
                                });
                    }
                });
    }

    private void log(String text){
        Log.d("Communication.java", text);}

    public void onReceiveMessage(String content) {
        chatReference.receiveMessage(content);
    }

    public void publishMessage(String content){
        if (!content.equals("")) {
            client.publishWith()
                    .topic(TOPIC)
                    .qos(MqttQos.EXACTLY_ONCE)
                    //.retain(false)
                    .payload(content.getBytes())
                    .messageExpiryInterval(2628000)
                    .send()
                    .whenComplete((publish, throwable) -> {
                        if (throwable != null) {
                            log("Publication failed");
                        } else {
                            log("Publication successful");
                        }
                    });
        }
    }

    public void disconnect(){
        client.disconnect();
    }

}
