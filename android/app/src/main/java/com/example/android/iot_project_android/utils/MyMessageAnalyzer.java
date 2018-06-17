package com.example.axilleastr.iot_project_android.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.example.axilleastr.iot_project_android.IotApp;
import com.example.axilleastr.iot_project_android.R;
import com.example.axilleastr.iot_project_android.fragments.DeviceFragment;
import com.example.axilleastr.iot_project_android.fragments.LoginFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;

public class MyMessageAnalyzer {


    private final static String TAG = MyMessageAnalyzer.class.getName();
    private static MyMessageAnalyzer instance;
    private final Context context;
    private final IotApp app;

    private MyMessageAnalyzer(Context context) {
        this.context = context;
        app = (IotApp) context.getApplicationContext();
    }

    public static MyMessageAnalyzer getInstance(Context context) {
        if (instance == null) {
            instance = new MyMessageAnalyzer(context);
        }
        return instance;
    }


    public void analyzeMessage(String payload, String topic) throws JSONException {
        Log.d(TAG, ".analyzeMessage() entered");
        JSONObject top = new JSONObject(payload);
        JSONObject d = top.getJSONObject("d");

       if (topic.contains(Constants.LIGHT_EVENT))
       {
            Log.d(TAG, "Light Event");
            // Set light on or off, or toggle light otherwise.
            String light = d.optString("light");
            final Boolean newState;
            if ("on".equals(light)) {
                newState = true;
            } else if ("off".equals(light)) {
                newState = false;
            } else {
                newState = null;
            }
           Intent lightIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
           lightIntent.putExtra(Constants.INTENT_DATA, Constants.LIGHT_EVENT);
           lightIntent.putExtra(Constants.INTENT_DATA_MESSAGE, d.getString("light"));
           context.sendBroadcast(lightIntent);
           app.handleLightMessage(newState);

        }
        else if (topic.contains(Constants.SOUND_EVENT))
        {
            Log.d(TAG, "Sound Event");
            // Set light on or off, or toggle light otherwise.
            String sound = d.optString("sound");
            final Boolean newState;
            if ("on".equals(sound)) {
                newState = true;
            } else if ("off".equals(sound)) {
                newState = false;
            } else {
                newState = null;
            }

            Intent soundIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
            soundIntent.putExtra(Constants.INTENT_DATA, Constants.SOUND_EVENT);
            soundIntent.putExtra(Constants.INTENT_DATA_MESSAGE,sound);
            context.sendBroadcast(soundIntent);
            app.handleSoundMessage(newState);
            Thread soundoff = new Thread(new Runnable() {
                public void run(){
                    try {
                        Thread.sleep(11200);
                        Intent soundIntentstop = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
                        soundIntentstop.putExtra(Constants.INTENT_DATA, Constants.SOUND_EVENT);
                        soundIntentstop.putExtra(Constants.INTENT_DATA_MESSAGE,"Off");
                        context.sendBroadcast(soundIntentstop);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            soundoff.start();

        }
        else if (topic.contains(Constants.VIBRATE_EVENT))
        {
            Log.d(TAG, "Vibrate Event");
            // Set light on or off, or toggle light otherwise.
            String vibrate = d.optString("vibrate");
            final Boolean newState;
            if ("on".equals(vibrate)) {
                newState = true;
            } else if ("off".equals(vibrate)) {
                newState = false;
            } else {
                newState = null;
            }

            Intent vibrateIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
            vibrateIntent.putExtra(Constants.INTENT_DATA, Constants.VIBRATE_EVENT);
            vibrateIntent.putExtra(Constants.INTENT_DATA_MESSAGE,vibrate);
            context.sendBroadcast(vibrateIntent);
            app.handleVibrateMessage(newState);
            Thread vibrateoff = new Thread(new Runnable() {
                     public void run(){
                         try {
                             Thread.sleep(4200);
                             Intent vibrateIntentstop = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
                             vibrateIntentstop.putExtra(Constants.INTENT_DATA, Constants.VIBRATE_EVENT);
                             vibrateIntentstop.putExtra(Constants.INTENT_DATA_MESSAGE,"off");
                             context.sendBroadcast(vibrateIntentstop);
                         } catch (InterruptedException e) {
                             e.printStackTrace();
                         }
                     }
            });
            vibrateoff.start();

        }
        else if (topic.contains(Constants.REPORT_EVENT))
        {
            Log.d(TAG, "Report Event");
            String report = d.optString("report");
            Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
            actionIntent.putExtra(Constants.INTENT_DATA, Constants.REPORT_EVENT);
            actionIntent.putExtra(Constants.INTENT_DATA_REPORT_MESSAGE,report);
            context.sendBroadcast(actionIntent);

        }
        else if (topic.contains(Constants.TEXT_EVENT))
        {
            int unreadCount = app.getUnreadCount();
            String messageText = d.getString("text");
            app.setUnreadCount(++unreadCount);

            // Send intent to LOG fragment to mark list data invalidated
            String runningActivity = app.getCurrentRunningActivity();
            //if (runningActivity != null && runningActivity.equals(LogPagerFragment.class.getName())) {
            Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOG);
            actionIntent.putExtra(Constants.INTENT_DATA, Constants.TEXT_EVENT);
            context.sendBroadcast(actionIntent);

            Intent unreadIntent;
            if (runningActivity.equals(LoginFragment.class.getName())) {
                unreadIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOGIN);
            } else if (runningActivity.equals(DeviceFragment.class.getName())) {
                unreadIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
            } else {
                return;
            }

            if (messageText != null) {
                unreadIntent.putExtra(Constants.INTENT_DATA, Constants.UNREAD_EVENT);
                context.sendBroadcast(unreadIntent);
            }
        }
        else if (topic.contains(Constants.ALERT_EVENT))
        {

            int unreadCount = app.getUnreadCount();
            String messageText = d.getString("text");
            app.setUnreadCount(++unreadCount);

            String runningActivity = app.getCurrentRunningActivity();
            if (runningActivity != null) {
                Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOG);
                actionIntent.putExtra(Constants.INTENT_DATA, Constants.TEXT_EVENT);
                context.sendBroadcast(actionIntent);

                Intent alertIntent;
                if (runningActivity.equals(LoginFragment.class.getName())) {
                    alertIntent = new Intent(Constants.APP_ID + Constants.INTENT_LOGIN);
                } else if (runningActivity.equals(DeviceFragment.class.getName())) {
                    alertIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
                } else {
                    return;
                }

                if (messageText != null) {
                    alertIntent.putExtra(Constants.INTENT_DATA, Constants.ALERT_EVENT);
                    alertIntent.putExtra(Constants.INTENT_DATA_MESSAGE, d.getString("text"));
                    context.sendBroadcast(alertIntent);
                }
            }
        }
    }
}