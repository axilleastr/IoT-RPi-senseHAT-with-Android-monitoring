using System.Threading.Tasks;
using Microsoft.ServiceBus.Messaging;
using System;
using System.Collections.Generic;
using System.Threading;
using Newtonsoft.Json;
using static southbound_API.API;

namespace CloudInteractions
{
    public class ReadDeviceToAzureMessages
    {
        //Azure IoT Hub info
        private static string connectionString = "<your Connection string---primary key>";
        private static string iotHubD2cEndpoint = "messages/events";
        private static EventHubClient eventHubClient;
        //

        public static List<Value> HumidityValues;
        public static List<Value> TemperatureValues;
        public static List<IoTDevice> AzureDevices;

        public ReadDeviceToAzureMessages(List<IoTDevice> azureDevices, List<Value> temperatureValues, List<Value> humidityValues)
        {
            ReadDeviceToAzureMessages.HumidityValues = humidityValues;
            ReadDeviceToAzureMessages.AzureDevices = azureDevices;
            ReadDeviceToAzureMessages.TemperatureValues = temperatureValues;

            Console.WriteLine("Received messages: (Ctrl-C to exit.)\n");
            eventHubClient = EventHubClient.CreateFromConnectionString(connectionString, iotHubD2cEndpoint);

            var d2cPartitions = eventHubClient.GetRuntimeInformation().PartitionIds;

            CancellationTokenSource cts = new CancellationTokenSource();

            System.Console.CancelKeyPress += (s, e) =>
            {
                e.Cancel = true;
                cts.Cancel();
                Console.WriteLine("Exiting...");
            };

            var tasks = new List<Task>();
            foreach (string partition in d2cPartitions)
            {
                tasks.Add(ReceiveMessagesFromDeviceAsync(partition, cts.Token));
            }
            Task.WaitAll(tasks.ToArray());
        }

        private static async Task ReceiveMessagesFromDeviceAsync(string partition, CancellationToken ct)
        {
            var eventHubReceiver = eventHubClient.GetDefaultConsumerGroup().CreateReceiver(partition, DateTime.UtcNow);
            while (true)
            {
                if (ct.IsCancellationRequested) break;
                EventData eventData = await eventHubReceiver.ReceiveAsync();
                if (eventData == null) continue;

                string data = System.Text.Encoding.UTF8.GetString(eventData.GetBytes());
                Console.WriteLine("\nMessage received. Partition: {0} Data: '{1}'", partition, data);

                var messageInfo = JsonConvert.DeserializeObject<IoTDevice>(data);
                var messageAlerts = JsonConvert.DeserializeObject<Alerts>(data);

                //Check if AzureDevices List is empty
                if (AzureDevices.Count == 0)
                {
                    AzureDevices.Add(messageInfo);
                    var DeviceTemp = new Value(messageInfo.deviceId, messageInfo.temperature);
                    var DeviceHum = new Value(messageInfo.deviceId, messageInfo.humidity);
                    TemperatureValues.Add(DeviceTemp);
                    HumidityValues.Add(DeviceHum);
                    Console.WriteLine("New Device on the DeviceList.");
                }
                //Check if device exists on List
                else
                {
                    foreach (var device in AzureDevices.ToArray())
                    {
                        //if device exists,do update values
                        if (messageInfo.deviceId == device.deviceId)
                        {
                            Console.WriteLine("Device is already on the DeviceList.Updating the values...");
                            #region update
                            device.temperature = messageInfo.temperature;
                            device.humidity = messageInfo.humidity;
                            device.messageId = messageInfo.messageId;
                            var DeviceTemp = new Value(messageInfo.deviceId, messageInfo.temperature);
                            var DeviceHum = new Value(messageInfo.deviceId, messageInfo.humidity);
                            TemperatureValues.Add(DeviceTemp);
                            HumidityValues.Add(DeviceHum);
                            #endregion
                            break;
                        }
                        else
                        {
                            //if device not exists,do add device
                            if (device == AzureDevices[AzureDevices.Count - 1])
                            {
                                AzureDevices.Add(messageInfo);
                                var DeviceTemp = new Value(messageInfo.deviceId, messageInfo.temperature);
                                var DeviceHum = new Value(messageInfo.deviceId, messageInfo.humidity);
                                TemperatureValues.Add(DeviceTemp);
                                HumidityValues.Add(DeviceHum);
                                Console.WriteLine("New Device on the DeviceList.");
                            }
                        }
                    }
                }
                //Check if should send Command to IBM Smartphone
                await Task.Run(() =>
                {
                    IBMMessages.CheckToSendCommand(messageInfo.temperature, messageInfo.humidity, messageAlerts);
                }
                 );
                //Print DeviceList
                Console.WriteLine("Last DeviceList Info: ");
                for (int i = 0; i < AzureDevices.Count; i++)
                {
                    Console.WriteLine(JsonConvert.SerializeObject(AzureDevices[i]));
                }
            }
        }
    }
}
