# IoT-RPi-senseHAT-with-Android-monitoring

The goal of this project was the connection and monitoring of a Raspberry Pi with the Azure IoT Hub, the modeling of an Android Application and connection with the IBM IoT Foundation, the design of a specific architecture of the mutual cooperation of the above with a  Desktop Application, as well as the development of a REST API for the connection with third party providers. 


## Getting Started
These instructions will try to help you build the project on your own.I will try to be quick and simple.
### Equipment Used
```
Raspberry Pi 3 
Sense Hat board  
Android Phone 
Laptop 
```
### Libraries Used 
```
•   For Desktop Application: 
IBMWIoTP, Microsoft.ServiceBus, Newtonsoft.Json, Nancy, southbound_API 
•   For Android Application 
org.eclipse.paho.android.service.jar, org.eclipse.paho.client.mqttv3.jar 
•   For Device(Rpi-SenseHat) 
iothub_client, sense_hat, azure IoT Hub SDK for Python 
```


### Characteristics of the System

#### Device (RPi - Sense Hat)
```
•   Used: Temperature and Humidity Sensors, 
8×8 LED matrix
•   User sets Temperature and Humidity 
ALERT thresholds
•   User sets Message Timespan for Report on 
Azure IoT Hub
```

#### Console Application
```
•   Reads Device to Azure Cloud Messages
•   Publishes Commands to Smartphone based on sensor Alerts
•   Reads Smartphone Report Event and sends feedback
•   Supports a specific REST API for Third Party Libraries
(with Authorization Key)
```

#### Android Application
```
•   Reads Commands from Console App and actuates based on sensor Alerts:
•   On Temperature Alert:
  •   Flash Light On/Off
  •   Vibration
•   On Humidity Alert:
  •   Sound Alert
•   Sends Report Request to Console App for Sensor Values
```


### Configuring Device 
**Connection of Sense HAT with Raspberry Pi** 
* First make sure you install the Raspbian operating system for RPi, and enable SSH and I2C 
* Push the Sense HAT carefully onto the pins of your Raspberry Pi, and secure it with the remaining screws 
* Connect to your RPi (via PuTTy or LCD Screen) and type the following commands: 
```
sudo apt-get update 
sudo apt-get install sense-hat 
sudo reboot 
```
 
**Connection with Azure IoT Hub** 
* Create an IoT hub 
* Register a device in the IoT hub for your device 
* Run the application on Pi 
* Clone the application by running the following command: 
```
cd ~ 
git clone https://github.com/nikoshet/IoT-RPi-senseHAT-with-Androidmonitoring/iot-hub-python-raspberrypi-client-app.git 
```
* Open the config file by running the following commands: 
```
cd iot-hub-python-raspberrypi-client-app 
nano config.py 
```
In the config.py file you can set Temperature and Humidity ALERT thresholds, and Message Timespan for Report on Azure IoT Hub. 
* Build the sample application by running the following command: 
```
sudo chmod u+x setup.sh 
sudo ./setup.sh 
```
* Run the sample application by running the following command: 
```
python app.py '<your Azure IoT hub device connection string>' 
```
Now your device sends temperature and humidity data on the IoT Hub. 


### Configuring Android App 
**Connection with IBM IoT Watson Platform** 

If you want to connect your smartphone with the IBMIoT Watson Platform the only thing you have to do , is to create an IBM account , and visit this uri  
https://your_org_id.internetofthings.ibmcloud.com/dashboard/#/boards/ 
(replace org_id with your account org_id)<br> 
Follow the next steps: 
* Create a Device , to get device_id and useful keys for the connection ( the only permission if you have android smartphone , is to modify the Device Type as “Android” , restrictively. )  
* If you want to create a Desktop Application, or to use the one developed , so as to send commands to your smartphone , you have also to create an app , with role → Backend Trusted Application ( the role you will choose is important!) , in order to connect as an ApplicationClient and interact with your smartphone device. 
 
 
**To Configure the App**

There are 2 ways to do that: 
* 1:
   * Clone the application by running the following command (or Download): 
    ```
    git clone https://github.com/nikoshet/IoT-RPi-senseHAT-with-Androidmonitoring/android.git 
    ```
    This android app can connect to Watson IoTP with custom  modification on the source code at class StarterFragment.java, and to be more specific to the function handleActivate() (line 148 )  , as shown below (so as to speed up the process of connection with the platform) : 
     ```
    app.setDeviceId("<your Device Id>");    // your Device Id 
    app.setOrganization("<your Device Id>");  // your Organization Id 
    app.setAuthToken("<your Device Id>");  // your AuthToken 
    ``` 
   * Also with this app you can connect to every Watson IoTP account ( device ) by giving your inputs-keys each time. 
After the modification of the code , so as to use the direct connection to your Watson IoTP ,follow the next steps:  
   * Enable USB Debugging on your smartphone (settings for programmers) , connect it with your desktop / laptop through your USB cable  
   * Run the android app with the Android Studio, direct to your smartphone. 
* 2:
   * You can also download from your smartphone and use this app , direct from the apk file ` app-debug.apk `  and connect to every Watson IoTP device by giving your inputs-keys each time.

### Configuring Desktop App 
* Clone the application by running the following command (or Download): 
```
git clone https://github.com/nikoshet/IoT-RPi-senseHAT-with-Androidmonitoring/DesktopApp.git 
```
* Open the project on Visual Studio

**Connection with Azure IoT Hub and IBM Watson IoTP**
* On the library “CloudInteractions”, on ReadDeviceToAzureMessages.cs set your IoT Hub info as shown below: 
```
private static string connectionString = "<your Connection string---primary key>"; 
private static string iotHubD2cEndpoint = "messages/events"; 
private static EventHubClient eventHubClient; 
``` 
* On the library “CloudInteractions”, on IBMMessages.cs set your IBM Watson IoTP info as shown below:
``` private static string orgId = "<your orgId>"; 
private static string appId = "<your appId>"; 
private static string apiKey = "<your apiKey>"; 
private static string authToken = "<your authToken>"; 
private static string deviceType = "<your deviceType>"; 
private static string deviceId = "<your deviceId>"; 
private static string format = "json" ; 
static ApplicationClient applicationClient = new ApplicationClient(orgId, appId, apiKey, authToken); 
```  
 
**For the REST API** 
<br>Uris used: 
```  
/getstate/{deviceId}/temperature 
/getstate/{deviceId}/humidity 
/getstate/{deviceId}/temperature/all 
/getstate/{deviceId}/humidity/all 
/get/devices
```  

## Miscellaneous
#### Sense HAT
[Here](https://raspberrypi.dk/wp-content/uploads/2015/08/raspberry-pi-sense-hat.pdf) you will find more information about the sense HAT .


#### Azure IoT Hub
More info on how to create an IoT Hub and register a device can be found [here](https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-raspberry-pi-kit-pythonget-started) . 


#### IBM Watson IoT Platform
More info on how to create an IBM account and create a device can be found [here](https://console.bluemix.net/docs/services/IoT/getting-started.html#getting-started-with-iotp) .
