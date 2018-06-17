package com.example.axilleastr.iot_project_android.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;

import com.example.axilleastr.iot_project_android.IotApp;
import com.example.axilleastr.iot_project_android.R;

public class DeviceActivity extends Activity{
    public static final String TAG = DeviceActivity.class.getName();
    String msg = "Android : ";

  //  private static final int CONTENT_VIEW_ID = 10101010;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_activity);


      /*  if(savedInstanceState != null) {
            int tabIndex = savedInstanceState.getInt("tabIndex");
            pager.setCurrentItem(tabIndex, false);
            Log.d(TAG, "savedinstancestate != null: " + tabIndex);
        }*/

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Log.d(msg, "The onCreate() event");
    }


    @Override
    protected void onResume() {
        super.onResume();

         IotApp app = (IotApp)getApplication();// getApplication();

        Log.d(msg, "The onResume() event");
    }


    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(msg, "The onDestroy() event");
    }

   /* @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int tabIndex = pager.getCurrentItem();
        outState.putInt("tabIndex", tabIndex);
    }*/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, ".onConfigurationChanged entered()");
        super.onConfigurationChanged(newConfig);
    }




}


