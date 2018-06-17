package com.example.axilleastr.iot_project_android.iot_workers;

import java.util.HashSet;
import java.util.Set;

public class IoTDevice {
    private String deviceName;
    private String organization;
    private String deviceType;
    private String deviceID;
    private String authorizationToken;

    private static final String NAME = "name:";
    private static final String ORG = "org:";
    private static final String DEVICE_TYPE = "type:";
    private static final String DEVICE_ID = "deviceId:";
    private static final String AUTH_TOKEN= "authToken:";


    public IoTDevice(String deviceName, String organization, String deviceType, String deviceID, String authorizationToken) {
        this.deviceName = deviceName;
        this.organization = organization;
        this.deviceType = deviceType;
        this.deviceID = deviceID;
        this.authorizationToken = authorizationToken;
    }

    //Create a new IoTDevice instance.

    public IoTDevice(Set<String> profileSet) {
        for (String value : profileSet) {
            if (value.contains(NAME)) {
                this.deviceName = value.substring(NAME.length());
            } else if (value.contains(ORG)) {
                this.organization = value.substring(ORG.length());
            } else if (value.contains(DEVICE_TYPE)) {
                this.deviceType = value.substring((DEVICE_TYPE.length()));
            } else if (value.contains(DEVICE_ID)) {
                this.deviceID = value.substring(DEVICE_ID.length());
            } else if (value.contains(AUTH_TOKEN)) {
                this.authorizationToken = value.substring(AUTH_TOKEN.length());
            }
        }
    }


    public Set<String> convertToSet() {
        // Put the new profile into the store settings and remove the old stored properties.
        Set<String> deviceSet = new HashSet<String>();
        deviceSet.add(NAME + this.deviceName);
        deviceSet.add(ORG + this.organization);
        deviceSet.add(DEVICE_TYPE + this.deviceType);
        deviceSet.add(DEVICE_ID + this.deviceID);
        deviceSet.add(AUTH_TOKEN + this.authorizationToken);

        return deviceSet;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getOrganization() {
        return organization;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }
}