package com.example.axilleastr.iot_project_android.iot_workers;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public interface IoTActionListener extends IMqttActionListener {

    void onSuccess(IMqttToken asyncActionToken );

    void onFailure(IMqttToken asyncActionToken, Throwable exception);
}