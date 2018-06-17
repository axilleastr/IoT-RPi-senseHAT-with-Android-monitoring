package com.example.axilleastr.iot_project_android.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.axilleastr.iot_project_android.IotApp;
import com.example.axilleastr.iot_project_android.R;
import com.example.axilleastr.iot_project_android.activities.DeviceActivity;
import com.example.axilleastr.iot_project_android.activities.LoginActivity;
import com.example.axilleastr.iot_project_android.iot_workers.IoTClient;
import com.example.axilleastr.iot_project_android.utils.Constants;
import com.example.axilleastr.iot_project_android.utils.IbmIotActionListener;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.security.Policy;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class DeviceFragment extends Fragment {

    private final static String TAG = DeviceFragment.class.getName();
    Context context;
    IotApp app;
    BroadcastReceiver broadcastReceiver;

    public static Button turnoffFlash , reportButton ,disconnectButton ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.device_fragment, container, false);
    }


    @Override
    public void onResume() {
        Log.d(TAG, ".onResume() entered");
        super.onResume();
        app = (IotApp) getActivity().getApplication();
        app.setCurrentRunningActivity(TAG);

        if (broadcastReceiver == null) {
            Log.d(TAG, ".onResume() - Registering iotBroadcastReceiver");
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, ".onReceive() - Received intent for iotBroadcastReceiver");
                    processIntent(intent);
                }
            };
        }

        getActivity().getApplicationContext().registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.APP_ID + Constants.INTENT_IOT));

        initializeDeviceActivity();
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


    private void initializeDeviceActivity() {
        Log.d(TAG, ".initializeDeviceFragment() entered");

        context = getActivity().getApplicationContext();

        initializeButtons();
    }


    private void initializeButtons() {
        Log.d(TAG, ".initializeButtons() entered");

        reportButton = (Button) getActivity().findViewById(R.id.Report);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Report pressed");
                sendReportEvent();
            }
        });

        disconnectButton = (Button) getActivity().findViewById(R.id.Disconnect);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("disconnect pressed");
                disconnect();
            }
        });

        turnoffFlash = (Button) getActivity().findViewById(R.id.TurnOffFlash);
        turnoffFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("turn off flash button pressed");
                closeFlashLight();
            }
        });

        turnoffFlash.setVisibility(View.GONE);
    }

    private void disconnect() {
        try {
            IbmIotActionListener listener = new IbmIotActionListener(context, Constants.ActionStateStatus.DISCONNECTING);
            IoTClient iotClient = IoTClient.getInstance(context, app.getOrganization(), app.getDeviceId(), app.getDeviceType(), app.getAuthToken());
            iotClient.disconnectDevice(listener);
            Intent i = new Intent(getActivity(),LoginActivity.class);
            startActivity(i);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sendReportEvent() {
        try {
            // create ActionListener to handle message published results
            IbmIotActionListener listener = new IbmIotActionListener(context, Constants.ActionStateStatus.PUBLISH);
            IoTClient iotClient = IoTClient.getInstance(context);
            iotClient.publishEvent(Constants.TEXT_EVENT, "json", Constants.REPORT_EVENT, 0, false, listener);

        } catch (MqttException e) {
            // Publish failed
            e.printStackTrace();
        }
    }

    private void closeFlashLight() {
        try
        {
            app.handleLightMessage(false);
            turnoffFlash.setVisibility(View.GONE);
            Intent lightIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
            lightIntent.putExtra(Constants.INTENT_DATA, Constants.LIGHT_EVENT);
            lightIntent.putExtra(Constants.INTENT_DATA_MESSAGE,"off");
            context.sendBroadcast(lightIntent);
        }
        catch (Exception e)
        { }
    }


    private void processIntent(Intent intent) {
        Log.d(TAG, ".processIntent() entered");

        String data = intent.getStringExtra(Constants.INTENT_DATA);
        System.out.println("\n \n \n \n \n \n \n \n \n \n " +data + "\n \n \n \n \n \n \n \n \n");
        assert data != null;

       if (data.equals(Constants.INTENT_DATA_RECEIVED))
       {
            processReceiveIntent();
        }
        else if(data.equals((Constants.LIGHT_EVENT)))
        {
            Log.d(TAG, "Updating light state textfield");
            String message = intent.getStringExtra(Constants.INTENT_DATA_MESSAGE);
            String light_state = "Flash light state : " + message ;
            ((TextView) getActivity().findViewById(R.id.Flash_state)).setText(light_state);
        }
        else if(data.equals((Constants.SOUND_EVENT)))
        {
            Log.d(TAG, "Updating sound state textfield");
            String message = intent.getStringExtra(Constants.INTENT_DATA_MESSAGE);
            String sound_state = "Sound Alert state : " + message ;
            ((TextView) getActivity().findViewById(R.id.Music_state)).setText(sound_state);
        }
        else if(data.equals((Constants.VIBRATE_EVENT))){
            Log.d(TAG, "Updating vibrate state textfield");
            String message = intent.getStringExtra(Constants.INTENT_DATA_MESSAGE);
            String vibrate_state = "Vibration state : " + message ;
            ((TextView) getActivity().findViewById(R.id.Vibrate_state)).setText(vibrate_state);
        }
        else if(data.equals((Constants.REPORT_EVENT))){
            Log.d(TAG, "receive report");
            String report_message = intent.getStringExtra(Constants.INTENT_DATA_REPORT_MESSAGE);
            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.report_dialog_title))
                    .setMessage(report_message)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }).show();
        }
        else if (data.equals(Constants.ALERT_EVENT)) {
            String message = intent.getStringExtra(Constants.INTENT_DATA_MESSAGE);
            new AlertDialog.Builder(getActivity())
                    .setTitle(getResources().getString(R.string.alert_dialog_title))
                    .setMessage(message)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }).show();
        }
        /*else if (data.equals(Constants.CALL_DATA)){
            startActivity(intent);
        }*/
    }

    private void processReceiveIntent() {
        Log.v(TAG, ".processReceiveIntent() entered");
        String receivedString = this.getString(R.string.messages_received);
        receivedString = receivedString.replace("0",Integer.toString(app.getReceiveCount()));
        ((TextView) getActivity().findViewById(R.id.messagesReceivedView)).setText(receivedString);
    }
    

 

}
