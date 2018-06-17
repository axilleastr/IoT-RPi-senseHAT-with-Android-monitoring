package com.example.axilleastr.iot_project_android.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;

import com.example.axilleastr.iot_project_android.R;

public class LoginActivity extends Activity {
    public static final String TAG = LoginActivity.class.getName();
    String msg = "Android : ";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Log.d(msg, "The onCreate() event");
    }


    @Override
    protected void onResume() {
        super.onResume();

       // IotApp app = new IotApp();// getApplication();

        Log.d(msg, "The onResume() event");
    }


    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(msg, "The onDestroy() event");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, ".onConfigurationChanged entered()");
        super.onConfigurationChanged(newConfig);
    }
}

