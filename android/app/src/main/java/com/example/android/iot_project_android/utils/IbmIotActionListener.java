package com.example.axilleastr.iot_project_android.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.axilleastr.iot_project_android.IotApp;
import com.example.axilleastr.iot_project_android.fragments.LoginFragment;
import com.example.axilleastr.iot_project_android.iot_workers.IoTActionListener;
import com.example.axilleastr.iot_project_android.iot_workers.IoTClient;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

public class IbmIotActionListener implements IoTActionListener {


    private final static String TAG = IbmIotActionListener.class.getName();

    private final Context context;
    private final Constants.ActionStateStatus action;
    private final IotApp app;


    public IbmIotActionListener(Context context, Constants.ActionStateStatus action) {
        this.context = context;
        this.action = action;
        app = (IotApp) context.getApplicationContext();
    }


    @Override
    public void onSuccess(IMqttToken token) {
        Log.d(TAG, ".onSuccess() entered");
        switch (action) {
            case CONNECTING:
                handleConnectSuccess();
                break;

            case SUBSCRIBE:
                handleSubscribeSuccess();
                break;

            case PUBLISH:
                handlePublishSuccess();
                break;

            case DISCONNECTING:
                handleDisconnectSuccess();
                break;

            default:
                break;
        }
    }



    @Override
    public void onFailure(IMqttToken token, Throwable throwable) {
        Log.e(TAG, ".onFailure() entered");
        switch (action) {
            case CONNECTING:
                handleConnectFailure(throwable);
                break;

            case SUBSCRIBE:
                handleSubscribeFailure(throwable);
                break;

            case PUBLISH:
                handlePublishFailure(throwable);
                break;

            case DISCONNECTING:
                handleDisconnectFailure(throwable);
                break;

            default:
                break;
        }
    }


    private void handleConnectSuccess() {
        Log.d(TAG, ".handleConnectSuccess() entered");

        app.setConnected(true);

        if (app.getConnectionType() != Constants.ConnectionType.QUICKSTART) {
            // create ActionListener to handle message published results
            try {
                IbmIotActionListener listener = new IbmIotActionListener(context, Constants.ActionStateStatus.PUBLISH);
                IoTClient iotClient = IoTClient.getInstance(context);
                iotClient.subscribeToCommand("+", "json", 0, "subscribe", listener);
            }catch (MqttException e) {
                Log.d(TAG, ".handleConnectSuccess() received exception on subscribeToCommand()");
            }
        }

        String runningActivity = app.getCurrentRunningActivity();
        Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOGIN);
        actionIntent.putExtra(Constants.INTENT_DATA, Constants.INTENT_DATA_CONNECT);
        context.sendBroadcast(actionIntent);
        System.out.println("\n \n \n \n----------sended contexxx--------------\n \n \n \n ");

    }


    private void handleSubscribeSuccess() {
        Log.d(TAG, ".handleSubscribeSuccess() entered");
    }


    private void handlePublishSuccess() {
        Log.d(TAG, ".handlePublishSuccess() entered");
    }


    private void handleDisconnectSuccess() {
        Log.d(TAG, ".handleDisconnectSuccess() entered");

        app.setConnected(false);
    }


    private void handleConnectFailure(Throwable throwable) {
        Log.e(TAG, ".handleConnectFailure() entered");
        Log.e(TAG, ".handleConnectFailure() - Failed with exception", throwable );
        throwable.printStackTrace();

        app.setConnected(false);

        //broadcast disconnect event
        String runningActivity = app.getCurrentRunningActivity();
        Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOGIN);
        actionIntent.putExtra(Constants.INTENT_DATA, Constants.INTENT_DATA_DISCONNECT);
        context.sendBroadcast(actionIntent);


        //also broadcast an alert event so user sees error message
        String NL = System.getProperty("line.separator");
        String errMsg = "Failed to connect to Watson IoT: "+NL+throwable;
        Intent alertIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOGIN);
        alertIntent.putExtra(Constants.INTENT_DATA, Constants.ALERT_EVENT);
        alertIntent.putExtra(Constants.INTENT_DATA_MESSAGE, errMsg);
        context.sendBroadcast(alertIntent);

        Log.e(TAG, ".handleConnectFailure() exit");
    }


    private void handleSubscribeFailure(Throwable throwable) {
        Log.e(TAG, ".handleSubscribeFailure() entered");
        Log.e(TAG, ".handleSubscribeFailure() - Failed with exception", throwable.getCause());
    }

    private void handlePublishFailure(Throwable throwable) {
        Log.e(TAG, ".handlePublishFailure() entered");
        Log.e(TAG, ".handlePublishFailure() - Failed with exception", throwable.getCause());
    }

    private void handleDisconnectFailure(Throwable throwable) {
        Log.e(TAG, ".handleDisconnectFailure() entered");
        Log.e(TAG, ".handleDisconnectFailure() - Failed with exception", throwable.getCause());
    }

}