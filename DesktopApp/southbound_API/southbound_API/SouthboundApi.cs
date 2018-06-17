
using System.Collections.Generic;

namespace southbound_API
{
    public class API
    {
        public static List<IoTDevice> AzureDevices = new List<IoTDevice>();
        public static List<Alerts> MyAlerts = new List<Alerts>();
        public static List<Value> TemperatureValues = new List<Value>();
        public static List<Value> HumidityValues = new List<Value>();

        public class IoTDevice
        {
            public string deviceId;
            public int messageId;
            public string temperature;
            public string humidity;

            #region IoTDevice Constructor
            public IoTDevice(string deviceId, int messageId, string temperature, string humidity)
            {
                this.deviceId = deviceId;
                this.messageId = messageId;
                this.temperature = temperature;
                this.humidity = humidity;
            }
            #endregion

            #region Get_Methods
            public string getdeviceId()
            {
                return deviceId;
            }

            public string getTempState()
            {
                return temperature;
            }

            public string getHumState()
            {
                return humidity;
            }
            #endregion
        }

        public class Value
        {
            public string deviceId;
            public string State;

            #region Value Constructor
            public Value(string deviceId, string State)
            {
                this.deviceId = deviceId;
                this.State = State;
            }
            #endregion
        }

        public class Alerts
        {
            public string temperatureAlert;
            public string humidityAlert;

            #region Value Constructor
            public Alerts(string temperatureAlert, string humidityAlert)
            {
                this.temperatureAlert = temperatureAlert;
                this.humidityAlert = humidityAlert;
            }
            #endregion
        }

        public enum Alert
        {
            trueAlert,falseAlert
        }
        
    }
}
