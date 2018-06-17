package com.example.axilleastr.iot_project_android;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.axilleastr.iot_project_android.activities.DeviceActivity;
import com.example.axilleastr.iot_project_android.fragments.DeviceFragment;
import com.example.axilleastr.iot_project_android.iot_workers.IoTDevice;
import com.example.axilleastr.iot_project_android.utils.Constants;
import com.example.axilleastr.iot_project_android.utils.IbmIotCallbacks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IotApp extends Application {

    private final static String TAG = IotApp.class.getName();

    // Current activity of the application, updated whenever activity is changed
    private String currentRunningActivity;
    Context context;
    // IoT-ibm values
    private String organization;
    private String deviceType;
    private String deviceId;
    private String authToken;
    private Constants.ConnectionType connectionType;

    private SharedPreferences settings;
    private IbmIotCallbacks myIoTCallbacks;

    // Application state variables
    private boolean connected = false;
    private int receiveCount = 0;
    private int unreadCount = 0;

    private boolean isCameraOn = false;
    public static Camera camera;
    private String cameraId;

    @Override
    public void onCreate() {
        Log.d(TAG, ".onCreate() entered");
        super.onCreate();
        settings = getSharedPreferences(Constants.SETTINGS, 0);
        myIoTCallbacks = IbmIotCallbacks.getInstance(this);
    }


    @TargetApi(value = 23)
    public void handleLightMessage(Boolean newState) {
        Log.d(TAG, ".handleLightMessage() entered");
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Log.d(TAG, "FEATURE_CAMERA_FLASH true");
            boolean setCameraOn = newState == null ? !isCameraOn : newState.booleanValue();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                if (setCameraOn && !isCameraOn) {
                    try {
                        String[] cameraIds = manager.getCameraIdList();
                        for (int i = 0; i < cameraIds.length && !isCameraOn; i++) {
                            cameraId = cameraIds[i];
                            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
                            if (cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                                manager.setTorchMode(cameraId, true);
                                isCameraOn = true;
                            }

                        }
                    } catch (CameraAccessException e) {
                        Log.w(TAG, e);
                    }
                } else if (!setCameraOn && isCameraOn) {
                    try {
                        isCameraOn = false;
                        manager.setTorchMode(cameraId, false);
                    } catch (CameraAccessException e) {
                        Log.w(TAG, e);
                    }
                }
            } else {

                if (setCameraOn && !isCameraOn) {
                    Log.d(TAG, "FEATURE_CAMERA_FLASH true");
                    //camera=new Camera();
                    camera = Camera.open();
                    Camera.Parameters p = camera.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(p);

                    try {
                        camera.setPreviewTexture(new SurfaceTexture(0));
                    } catch (IOException e) {
                        Log.w(TAG, e);
                    }
                    DeviceFragment.turnoffFlash.setVisibility(View.VISIBLE);
                    camera.startPreview();

                  /* *//* Thread thread = new Thread() {
                        @Override
                        public void run() {*//*
                            try {
                                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+306983953345")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    *//*    }
                    };
                    thread.start();*/
                    isCameraOn = true;

                } else if (!setCameraOn && isCameraOn) {
                    camera.stopPreview();
                    camera.release();
                    DeviceFragment.turnoffFlash.setVisibility(View.GONE);
                    isCameraOn = false;
                }
            }
        } else {
            Log.d(TAG, "FEATURE_CAMERA_FLASH false");
        }
    }

    public void handleVibrateMessage(Boolean newState) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(newState){
            long[] pattern = {0,500, 100, 500, 100, 500, 100, 500, 100, 500 ,100 ,500 ,100 ,500 ,100};
            v.vibrate(pattern, -1);
        }
        else{ v.cancel();}
    }

    public void handleSoundMessage(Boolean newState) {
        MediaPlayer firealarm = new MediaPlayer().create(this,R.raw.firealarm);
        if(newState){
            firealarm.start();
        }
        else{ firealarm.pause();}
    }

    public void handleReportMessage(String report) {
        Intent actionIntent = new Intent(Constants.APP_ID + Constants.INTENT_IOT);
        actionIntent.putExtra(Constants.INTENT_DATA, Constants.INTENT_DATA_REPORT_MESSAGE);
        actionIntent.putExtra(Constants.INTENT_DATA_REPORT_MESSAGE,report);
        context.sendBroadcast(actionIntent);

    }
    // Getters and Setters
    public String getCurrentRunningActivity() { return currentRunningActivity; }

    public void setCurrentRunningActivity(String currentRunningActivity) { this.currentRunningActivity = currentRunningActivity; }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setConnectionType(Constants.ConnectionType type) {
        this.connectionType = type;
    }

    public Constants.ConnectionType getConnectionType() {
        return this.connectionType;
    }

    public int getReceiveCount() {
        return receiveCount;
    }

    public void setReceiveCount(int receiveCount) {
        this.receiveCount = receiveCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public IbmIotCallbacks getMyIoTCallbacks() {
        return myIoTCallbacks;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceType() {
        return deviceType;
    }



}