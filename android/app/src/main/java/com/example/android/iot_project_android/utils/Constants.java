package com.example.axilleastr.iot_project_android.utils;

public class Constants {


    public final static String APP_ID = "com.example.axilleastr.iot_project_android";
    public final static String SETTINGS = APP_ID+".Settings";

    public final static String M2M = "m2m";

    public static final String QUICKSTART = "quickstart";
    public final static String QUICKSTART_URL = "https://quickstart.internetofthings.ibmcloud.com/#/device/";

    public static final String LOGIN_LABEL = "LOGIN";
    public static final String IOT_LABEL = "IOT";
    public static final String LOG_LABEL = "LOG";

    public enum ConnectionType {
        M2M, QUICKSTART, IOTF
    }

    public static enum ActionStateStatus {
        CONNECTING, DISCONNECTING, SUBSCRIBE, PUBLISH
    }

    // IoT properties
    public final static String AUTH_TOKEN = "authtoken";
    public final static String DEVICE_ID = "deviceid";
    public final static String ORGANIZATION = "organization";
    public final static String DEVICE_TYPE = "Android";

    // IoT events and commands

    public final static String LIGHT_EVENT = "light";
    public final static String SOUND_EVENT = "sound";
    public final static String VIBRATE_EVENT = "vibrate";
    public final static String TEXT_EVENT = "text";
    public final static String ALERT_EVENT = "alert";
    public final static String UNREAD_EVENT = "unread";
    public final static String REPORT_EVENT = "report";

    public final static String CONNECTIVITY_MESSAGE = "connectivityMessage";
    public final static String ACTION_INTENT_CONNECTIVITY_MESSAGE_RECEIVED = Constants.APP_ID + "." + "CONNECTIVITY_MESSAGE_RECEIVED";

    // Fragment intents
    public final static String INTENT_LOGIN = "INTENT_LOGIN";
    public final static String INTENT_IOT = "INTENT_IOT";
    public final static String INTENT_LOG = "INTENT_LOG";


    public final static String INTENT_DATA = "data";
    public final static String CALL_DATA = "call_data";

    // MQTT action intent data
    public final static String INTENT_DATA_CONNECT = "connect";
    public final static String INTENT_DATA_DISCONNECT = "disconnect";
    public final static String INTENT_DATA_PUBLISHED = "publish";
    public final static String INTENT_DATA_RECEIVED = "receive";
    public final static String INTENT_DATA_MESSAGE = "message";
    public final static String INTENT_DATA_REPORT_MESSAGE = "report_message";

    public final static int ERROR_BROKER_UNAVAILABLE = 3;



}