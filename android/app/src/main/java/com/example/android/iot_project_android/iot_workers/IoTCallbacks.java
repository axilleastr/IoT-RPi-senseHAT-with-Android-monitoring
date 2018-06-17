package com.example.axilleastr.iot_project_android.iot_workers;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface IoTCallbacks  extends MqttCallback {

    void connectionLost(Throwable cause);


    void messageArrived(String topic, MqttMessage message) throws Exception;


    void deliveryComplete(IMqttDeliveryToken token);
}