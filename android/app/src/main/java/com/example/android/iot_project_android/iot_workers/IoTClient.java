package com.example.axilleastr.iot_project_android.iot_workers;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import javax.net.SocketFactory;

public class IoTClient {

    private static final String TAG = IoTClient.class.getName();
    private static final String IOT_ORGANIZATION_SSL = ".messaging.internetofthings.ibmcloud.com:8883";
    private static final String IOT_DEVICE_USERNAME  = "use-token-auth";

    private static IoTClient instance;
    private MqttAndroidClient client;
    private final Context context;

    private String organization;
    private String deviceType;
    private String deviceID;
    private String authorizationToken;

    private IoTClient(Context context) {
        this.context = context;
        this.client = null;
    }

    private IoTClient(Context context, String organization, String deviceID, String deviceType, String authorizationToken) {
        this.context = context;
        this.client = null;
        this.organization = organization;
        this.deviceID = deviceID;
        this.deviceType = deviceType;
        this.authorizationToken = authorizationToken;
    }


    public static IoTClient getInstance(Context context) {
        Log.d(TAG, ".getInstance() entered");
        if (instance == null) {
            instance = new IoTClient(context);
        }
        return instance;
    }


    public static IoTClient getInstance(Context context, String organization, String deviceID, String deviceType, String authorizationToken) {
        Log.d(TAG, ".getInstance() entered");
        if (instance == null) {
            instance = new IoTClient(context, organization, deviceID, deviceType, authorizationToken);
        } else {
            instance.setAuthorizationToken(authorizationToken);
            instance.setOrganization(organization);
            instance.setDeviceID(deviceID);
            instance.setDeviceType(deviceType);
        }
        return instance;
    }

    public IMqttToken connectDevice(IoTCallbacks callbacks, IoTActionListener listener, SocketFactory factory) throws MqttException {
        Log.d(TAG, ".connectDevice() entered");
        String clientID = "d:" + this.getOrganization() + ":" + this.getDeviceType() + ":" + this.getDeviceID();
        String connectionURI;
        connectionURI = "ssl://" + this.getOrganization() + IOT_ORGANIZATION_SSL;


        if (!isMqttConnected()) {
            if (client != null) {
                client.unregisterResources();
                client = null;
            }
            client = new MqttAndroidClient(context, connectionURI, clientID);
            client.setCallback(callbacks);

            String username = IOT_DEVICE_USERNAME;
            char[] password = this.getAuthorizationToken().toCharArray();

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(username);
            options.setPassword(password);

            if (factory != null && !this.getOrganization().equals("quickstart")) {
                options.setSocketFactory(factory);
            }

            Log.d(TAG, "Connecting to server: " + connectionURI);
            try {
                // connect
                return client.connect(options, context, listener);
            } catch (MqttException e) {
                Log.e(TAG, "Exception caught while attempting to connect to server", e.getCause());
                throw e;
            }
        }
        return null;
    }


    public IMqttToken disconnectDevice(IoTActionListener listener) throws MqttException {
        Log.d(TAG, ".disconnectDevice() entered");
        if (isMqttConnected()) {
            try {
                return client.disconnect(context, listener);
            } catch (MqttException e) {
                Log.e(TAG, "Exception caught while attempting to disconnect from server", e.getCause());
                throw e;
            }
        }
        return null;
    }

    public IMqttToken subscribeToEvent(String event, String format, int qos, Object userContext, IMqttActionListener listener) throws MqttException {
        Log.d(TAG, ".subscribeToEvent() entered");
        String eventTopic = getEventTopic(event, format);
        return subscribe(eventTopic, qos, userContext, listener);
    }


    public IMqttToken unsubscribeFromEvent(String event, String format, Object userContext, IMqttActionListener listener) throws MqttException {
        Log.d(TAG, ".unsubscribeFromEvent() entered");
        String eventTopic = getEventTopic(event, format);
        return unsubscribe(eventTopic, userContext, listener);
    }

    public IMqttToken subscribeToCommand(String command, String format, int qos, Object userContext, IMqttActionListener listener) throws MqttException {
        Log.d(TAG, "subscribeToCommand() entered");
        String commandTopic = getCommandTopic(command, format);
        return subscribe(commandTopic, qos, userContext, listener);
    }


    public IMqttToken unsubscribeFromCommand(String command, String format, Object userContext, IMqttActionListener listener) throws MqttException {
        Log.d(TAG, ".unsubscribeFromCommand() entered");
        String commandTopic = getCommandTopic(command, format);
        return unsubscribe(commandTopic, userContext, listener);
    }


    public IMqttDeliveryToken publishEvent(String event, String format, String payload, int qos, boolean retained, IoTActionListener listener) throws MqttException {
        Log.d(TAG, ".publishEvent() entered");
        String eventTopic = getEventTopic(event, format);
        return publish(eventTopic, payload, qos, retained, listener);
    }


    public IMqttDeliveryToken publishCommand(String command, String format, String payload, int qos, boolean retained, IoTActionListener listener) throws MqttException {
        Log.d(TAG, ".publishCommand() entered");
        String commandTopic = getCommandTopic(command, format);
        return publish(commandTopic, payload, qos, retained, listener);
    }

    // PRIVATE FUNCTIONS


    private IMqttToken subscribe(String topic, int qos, Object userContext, IMqttActionListener listener) throws MqttException {
        Log.d(TAG, ".subscribe() entered");
        if (isMqttConnected()) {
            try {
                return client.subscribe(topic, qos, userContext, listener);
            } catch (MqttException e) {
                Log.e(TAG, "Exception caught while attempting to subscribe to topic " + topic, e.getCause());
                throw e;
            }
        }
        return null;
    }


    private IMqttToken unsubscribe(String topic, Object userContext, IMqttActionListener listener) throws MqttException {
        Log.d(TAG, ".unsubscribe() entered");
        if (isMqttConnected()) {
            try {
                return client.unsubscribe(topic, userContext, listener);
            } catch (MqttException e) {
                Log.e(TAG, "Exception caught while attempting to subscribe to topic " + topic, e.getCause());
                throw e;
            }
        }
        return null;
    }


    private IMqttDeliveryToken publish(String topic, String payload, int qos, boolean retained, IoTActionListener listener) throws MqttException {
        Log.d(TAG, ".publish() entered");

        // check if client is connected
        if (isMqttConnected()) {
            // create a new MqttMessage from the message string
            MqttMessage mqttMsg = new MqttMessage(payload.getBytes());
            // set retained flag
            mqttMsg.setRetained(retained);
            // set quality of service
            mqttMsg.setQos(qos);
            try {
                // create ActionListener to handle message published results
                Log.d(TAG, ".publish() - Publishing " + payload + " to: " + topic + ", with QoS: " + qos + " with retained flag set to " + retained);
                return client.publish(topic, mqttMsg, context, listener);
            } catch (MqttPersistenceException e) {
                Log.e(TAG, "MqttPersistenceException caught while attempting to publish a message", e.getCause());
                throw e;
            } catch (MqttException e) {
                Log.e(TAG, "MqttException caught while attempting to publish a message", e.getCause());
                throw e;
            }
        }
        return null;
    }


    private boolean isMqttConnected() {
        Log.d(TAG, ".isMqttConnected() entered");
        boolean connected = false;
        try {
            if ((client != null) && (client.isConnected())) {
                connected = true;
            }
        } catch (Exception e) {
            // swallowing the exception as it means the client is not connected
        }
        Log.d(TAG, ".isMqttConnected() - returning " + connected);
        return connected;
    }


    public static String getEventTopic(String event, String format) {
        return "iot-2/evt/" + event + "/fmt/json";
    }


    public static String getCommandTopic(String command, String format) {
        return "iot-2/cmd/" + command + "/fmt/json";
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
