package com.example.axilleastr.iot_project_android.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.axilleastr.iot_project_android.IotApp;
import com.example.axilleastr.iot_project_android.iot_workers.IoTCallbacks;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;

public class IbmIotCallbacks implements IoTCallbacks {
    private final static String TAG = IbmIotCallbacks.class.getName();
    private final Context context;
    private final IotApp app;
    private static IbmIotCallbacks myIoTCallbacks;

    public IbmIotCallbacks(Context context) {
        this.app = (IotApp) context;
        this.context = context;
    }

    public static IbmIotCallbacks getInstance(Context context) {
        if (myIoTCallbacks == null) {
            myIoTCallbacks = new IbmIotCallbacks(context);
        }
        return myIoTCallbacks;
    }


    @Override
    public void connectionLost(Throwable throwable) {
        Log.e(TAG, ".connectionLost() entered");

        if (throwable != null) {
            throwable.printStackTrace();
        }

        app.setConnected(false);

        Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOGIN);
        actionIntent.putExtra(Constants.INTENT_DATA, Constants.INTENT_DATA_DISCONNECT);
        context.sendBroadcast(actionIntent);

    }


    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        Log.d(TAG, ".messageArrived() entered");

        int receiveCount = app.getReceiveCount();
        app.setReceiveCount(++receiveCount);
        Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
        actionIntent.putExtra(Constants.INTENT_DATA, Constants.INTENT_DATA_RECEIVED);
        context.sendBroadcast(actionIntent);

        String payload = new String(mqttMessage.getPayload());
        Log.d(TAG, ".messageArrived - Message received on topic " + topic
                + ": message is " + payload);
        // TODO: Process message
        try {
            // send the message through the application logic
            MyMessageAnalyzer.getInstance(context).analyzeMessage(payload, topic);
        } catch (JSONException e) {
            Log.e(TAG, ".messageArrived() - Exception caught while steering a message", e.getCause());
            e.printStackTrace();
        }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.d(TAG, ".deliveryComplete() entered");
    }
}