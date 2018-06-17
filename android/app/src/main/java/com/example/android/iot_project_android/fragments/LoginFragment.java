package com.example.axilleastr.iot_project_android.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.axilleastr.iot_project_android.IotApp;
import com.example.axilleastr.iot_project_android.R;
import com.example.axilleastr.iot_project_android.activities.DeviceActivity;
import com.example.axilleastr.iot_project_android.iot_workers.IoTClient;
import com.example.axilleastr.iot_project_android.utils.Constants;
import com.example.axilleastr.iot_project_android.utils.IbmIotActionListener;
import com.google.android.gms.security.ProviderInstaller;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.security.KeyStore;
import java.sql.Timestamp;
import java.util.Date;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class LoginFragment extends Fragment {


    private final static String TAG = LoginFragment.class.getName();
    Context context;
    IotApp app;
    BroadcastReceiver broadcastReceiver;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }


    @Override
    public void onResume() {
        Log.d(TAG, ".onResume() entered");

        super.onResume();
       // app = new IotApp();
        app = (IotApp) getActivity().getApplication();
        app.setCurrentRunningActivity(TAG);

        if (broadcastReceiver == null) {
            Log.d(TAG, ".onResume() - Registering loginBroadcastReceiver");
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, ".onReceive() - Received intent for loginBroadcastReceiver");
                    processIntent(intent);
                    System.out.println("\n \n \n \n"+intent+"\n \n \n \n");
                    Log.d(TAG, ".onReceive() - exit");
                }
            };
        }

        getActivity().getApplicationContext().registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.APP_ID + Constants.INTENT_LOGIN));

        // initialise
        initializeLoginActivity();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, ".onDestroy() entered");

        try {
            getActivity().getApplicationContext().unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException iae) {
            // Do nothing
        }
        super.onDestroy();
    }


    private void initializeLoginActivity() {
        Log.d(TAG, ".initializeLoginFragment() entered");

        context = getActivity().getApplicationContext();

        updateViewStrings();

        // setup button listeners
        initializeButtons();
    }



    void updateViewStrings() {
        Log.d(TAG, ".updateViewStrings() entered");
        if (app.getOrganization() != null) {
            ((EditText) getActivity().findViewById(R.id.organizationValue)).setText(app.getOrganization());
        }

        if (app.getDeviceId() != null) {
            ((EditText) getActivity().findViewById(R.id.deviceIDValue)).setText(app.getDeviceId());
        }

        if (app.getAuthToken() != null) {
            ((EditText) getActivity().findViewById(R.id.authTokenValue)).setText(app.getAuthToken());
        }

        if (app.isConnected()) {
            Log.d(TAG, "\n \n \n \n \n app connected entered \n \n \n \n \n ");
            updateConnectedValues();
            //processConnectIntent();
        }
    }


    private void initializeButtons() {
        Log.d(TAG, ".initializeButtons() entered");

        Button button = (Button) getActivity().findViewById(R.id.showTokenButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleShowToken();
            }
        });

        button = (Button) getActivity().findViewById(R.id.activateButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleActivate();
            }
        });



    }

    private boolean checkCanConnect() {

            app.setConnectionType(Constants.ConnectionType.IOTF);
            if (app.getOrganization() == null || app.getOrganization().equals("") ||
                    app.getDeviceId() == null || app.getDeviceId().equals("") ||
                    app.getAuthToken() == null || app.getAuthToken().equals("")) {
                return false;
            }

        return true;
    }

    private void displaySetPropertiesDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.connect_props_title))
                .setMessage(getResources().getString(R.string.connect_props_text))
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
    }


    private void handleActivate() {
        Log.d(TAG, ".handleActivate() entered");
        System.out.println("activate pressed");
        String buttonTitle = ((Button) getActivity().findViewById(R.id.activateButton)).getText().toString();
        Button activateButton = (Button) getActivity().findViewById(R.id.activateButton);
        app.setDeviceType(Constants.DEVICE_TYPE);
        app.setDeviceId(((EditText) getActivity().findViewById(R.id.deviceIDValue)).getText().toString());
        app.setOrganization(((EditText) getActivity().findViewById(R.id.organizationValue)).getText().toString());
        app.setAuthToken(((EditText) getActivity().findViewById(R.id.authTokenValue)).getText().toString());
        IoTClient iotClient = IoTClient.getInstance(context, app.getOrganization(), app.getDeviceId(), app.getDeviceType(), app.getAuthToken());
        activateButton.setEnabled(false);
        if (buttonTitle.equals(getResources().getString(R.string.activate_button)) && !app.isConnected()) {
            if (checkCanConnect()) {
                // create ActionListener to handle message published results
                try {
                    SocketFactory factory = null;

                        try {
                            ProviderInstaller.installIfNeeded(context);

                            SSLContext sslContext;
                            KeyStore ks = KeyStore.getInstance("bks");
                            ks.load(context.getResources().openRawResource(R.raw.iot), "password".toCharArray());
                            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
                            tmf.init(ks);
                            TrustManager[] tm = tmf.getTrustManagers();
                            sslContext = SSLContext.getInstance("TLSv1.2");
                            sslContext.init(null, tm, null);
                            factory = sslContext.getSocketFactory();
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }

                    System.out.println("try to connect now");
                    IbmIotActionListener listener = new IbmIotActionListener(context, Constants.ActionStateStatus.CONNECTING);
                    //start connection - if this method returns, connection has not yet happened
                    iotClient.connectDevice(app.getMyIoTCallbacks(), listener, factory);

                } catch (MqttException e) {
                    System.out.println("\n \n \n \n \n  exception wrong parametrs propably \n\n\n\n\n\n");
                    if (e.getReasonCode() == (Constants.ERROR_BROKER_UNAVAILABLE)) {
                        // error while connecting to the broker - send an intent to inform the user
                        Intent actionIntent = new Intent(Constants.ACTION_INTENT_CONNECTIVITY_MESSAGE_RECEIVED);
                        actionIntent.putExtra(Constants.CONNECTIVITY_MESSAGE, Constants.ERROR_BROKER_UNAVAILABLE);
                        context.sendBroadcast(actionIntent);
                    }
                }
            } else {
                displaySetPropertiesDialog();
                activateButton.setEnabled(true);
            }
        } else if (buttonTitle.equals(getResources().getString(R.string.deactivate_button)) && app.isConnected()) {
            // create ActionListener to handle message published results
            try {
                IbmIotActionListener listener = new IbmIotActionListener(context, Constants.ActionStateStatus.DISCONNECTING);
                iotClient.disconnectDevice(listener);
            } catch (MqttException e) {
                // Disconnect failed
            }
        }
        Log.d(TAG, ".handleActivate() exit");
    }


    private void handleShowToken() {
        Log.d(TAG, ".handleShowToken() entered");
        System.out.println("activate clicked");
        Button showTokenButton = (Button) getActivity().findViewById(R.id.showTokenButton);
        String buttonTitle = showTokenButton.getText().toString();
        EditText tokenText = (EditText) getActivity().findViewById(R.id.authTokenValue);
        if (buttonTitle.equals(getResources().getString(R.string.showToken_button))) {
            showTokenButton.setText(getResources().getString(R.string.hideToken_button));
            tokenText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else if (buttonTitle.equals(getResources().getString(R.string.hideToken_button))) {
            showTokenButton.setText(getResources().getString(R.string.showToken_button));
            tokenText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }


    private void processIntent(Intent intent) {
        Log.d(TAG, ".processIntent() entered");

        updateViewStrings();

        String data = intent.getStringExtra(Constants.INTENT_DATA);
        assert data != null;
        if (data.equals(Constants.INTENT_DATA_CONNECT)) {
            processConnectIntent();

            Intent i = new Intent(getActivity(),DeviceActivity.class);
            startActivity(i);


            //--------------------------------------------------------------------------
        } else if (data.equals(Constants.INTENT_DATA_DISCONNECT)) {
            processDisconnectIntent();
        } else if (data.equals(Constants.ALERT_EVENT)) {
            String message = intent.getStringExtra(Constants.INTENT_DATA_MESSAGE);

            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.alert_dialog_title))
                    .setMessage(message)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }).show();
        }
        Log.d(TAG, ".processIntent() exit");
    }

    private void processConnectIntent() {
        Log.d(TAG, ".processConnectIntent() entered");
        updateConnectedValues();

    }

    private void updateConnectedValues() {
        Button activateButton = (Button) getActivity().findViewById(R.id.activateButton);
        activateButton.setEnabled(true);
        String connectedString = this.getString(R.string.is_connected);
        connectedString = connectedString.replace("No", "Yes");
        ((TextView) getActivity().findViewById(R.id.isConnected)).setText(connectedString);
        activateButton.setText(getResources().getString(R.string.deactivate_button));
    }


    private void processDisconnectIntent() {
        Log.d(TAG, ".processDisconnectIntent() entered");
        Button activateButton = (Button) getActivity().findViewById(R.id.activateButton);
        activateButton.setEnabled(true);
        ((TextView) getActivity().findViewById(R.id.isConnected)).setText(this.getString(R.string.is_connected));
        activateButton.setText(getResources().getString(R.string.activate_button));

        Log.d(TAG, ".processDisconnectIntent() exit");
    }



}
