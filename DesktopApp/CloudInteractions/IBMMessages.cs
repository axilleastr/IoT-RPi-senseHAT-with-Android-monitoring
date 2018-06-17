using System;
using IBMWIoTP;
using System.Globalization;
using System.Threading;
using southbound_API;

namespace CloudInteractions
{
    public class IBMMessages
    {        
        //IBM Watson IoTP info
        private static string orgId = "<your orgId>";
        private static string appId = "<your appId>";
        private static string apiKey = "<your apiKey>";
        private static string authToken = "<your authToken>";
        private static string deviceType = "<your deviceType>";
        private static string deviceId = "<your deviceId>";

        //
        private static string format = "json";
        static ApplicationClient applicationClient = new ApplicationClient(orgId, appId, apiKey, authToken);
        public IBMMessages()
        {
            Console.WriteLine("Connecting to IBM WatsonIoTP..");
            try
            {
                applicationClient.connect();
                Console.WriteLine("Connected sucessfully to app id : " + appId);

                applicationClient.eventCallback += processEvent;
                applicationClient.subscribeToDeviceEvents(deviceType, deviceId, "report", format, 0);

            }
            catch (Exception)
            {
                // ignore
            }
        }

        public static void processEvent(string deviceType, string deviceId, string eventName, string format, string data)
        {
            Console.WriteLine("Sample Application Client : Device Type" + deviceType + " Device ID:" + deviceId + "Sample Event : " + eventName + " format : " + format + " data : " + data);
            if (data == "report")
            {
                //Check if DeviceList is empty
                if (ReadDeviceToAzureMessages.AzureDevices.Count != 0)
                {
                    RespondToReportEvent(ReadDeviceToAzureMessages.AzureDevices[ReadDeviceToAzureMessages.AzureDevices.Count - 1].temperature, ReadDeviceToAzureMessages.AzureDevices[ReadDeviceToAzureMessages.AzureDevices.Count - 1].humidity);
                }
                else
                {
                    Console.WriteLine("REPORT EVENT received.Empty Azure Device List.");
                    RespondToReportEventWithNullList();
                }
            }
        }
        
        public static void RespondToReportEvent(string temperature, string humidity)
        {
            float tempval = float.Parse(temperature, CultureInfo.InvariantCulture.NumberFormat);
            float humval = float.Parse(humidity, CultureInfo.InvariantCulture.NumberFormat);

            try
            {
                string data = "{\"d\":{\"report\":[{\"temperature\":\""+ tempval + "\"},{\"humidity\":\"" + humval + "\"}]}}";
                Console.WriteLine("publishing command for REPORT event...data: "+data);
                applicationClient.publishCommand(deviceType, deviceId, "report", format, data, 1);
                Console.WriteLine("Command published.");
            }
            catch (Exception)
            {
                // ignore
            }
        }

        public static void RespondToReportEventWithNullList()
        {
            try
            {
                string data = "{\"d\":{\"report\":\"No Values Yet.\"}}";
                Console.WriteLine("publishing command for REPORT event...data: " + data);
                applicationClient.publishCommand(deviceType, deviceId, "report", format, data, 1);
                Console.WriteLine("Command published.");
            }
            catch (Exception)
            {
                // ignore
            }
        }

        //Check if should send Command to IBM Smartphone
        public static void CheckToSendCommand(string temperature, string humidity , API.Alerts messageAlerts)
        {
            float tempval = float.Parse(temperature, CultureInfo.InvariantCulture.NumberFormat);
            float humval = float.Parse(humidity, CultureInfo.InvariantCulture.NumberFormat);

            //Connecting to IBM WatsonIoTP
            //Console.WriteLine("Connecting to IBM WatsonIoTP..");
            try
            {
                //ApplicationClient applicationClient = new ApplicationClient(orgId, appId, apiKey, authToken);
                //applicationClient.connect();
                //Console.WriteLine("Connected sucessfully to app id : " + appId);

                //Check if Tempperature>30
                if (messageAlerts.temperatureAlert == "true")
                { 
                    //TURN VIBRATION ON
                    string data2 = "{\"d\":{\"vibrate\":\"on\"}}";
                    Console.WriteLine("publishing command-TURN VIBRATION ON...");
                    applicationClient.publishCommand(deviceType, deviceId, "vibrate", format, data2, 1);
                    Console.WriteLine("Command published.");
                    Thread.Sleep(1000);
                    //TURN LIGHT ON
                    string data = "{\"d\":{\"light\":\"on\"}}";
                    Console.WriteLine("publishing command-TURN LIGHT ON...");
                    applicationClient.publishCommand(deviceType, deviceId, "light", format, data, 1);
                    Console.WriteLine("Command published.");
                }
                else if (messageAlerts.temperatureAlert == "false")
                {
                    //TURN VIBRATION OFF
                    string data2 = "{\"d\":{\"vibrate\":\"off\"}}";
                    Console.WriteLine("publishing command-TURN VIBRATION OFF...");
                    applicationClient.publishCommand(deviceType, deviceId, "vibrate", format, data2, 1);
                    Console.WriteLine("Command published.");
                    Thread.Sleep(1000);
                    //TURN LIGHT OFF
                    string data = "{\"d\":{\"light\":\"off\"}}";
                    Console.WriteLine("publishing command-TURN LIGHT OFF...");
                    applicationClient.publishCommand(deviceType, deviceId, "light", format, data, 1);
                    Console.WriteLine("Command published.");
                }
                Thread.Sleep(1000);
                //Check if Humidity>70
                if (messageAlerts.humidityAlert == "true")
                {
                    //TURN SOUND ON
                    string data = "{\"d\":{\"sound\":\"on\"}}";
                    Console.WriteLine("publishing command-TURN SOUND ON...");
                    applicationClient.publishCommand(deviceType, deviceId, "sound", format, data, 1);
                    Console.WriteLine("Command published.");
                }
                else if (messageAlerts.humidityAlert == "false")
                {
                    //TURN SOUND OFF
                    string data = "{\"d\":{\"sound\":\"off\"}}";
                    Console.WriteLine("publishing command-TURN SOUND OFF...");
                    applicationClient.publishCommand(deviceType, deviceId, "sound", format, data, 1);
                    Console.WriteLine("Command published.");
                }
                //Disonnecting from IBM WatsonIoTP
                //applicationClient.disconnect();
            }
            catch (Exception)
            {
                // ignore
            }
        }

    }
}
