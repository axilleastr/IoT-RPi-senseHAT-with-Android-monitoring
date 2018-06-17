using Nancy;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using static southbound_API.API;

namespace NancySample
{
    public class RestModule : NancyModule
    {
        //List to return
        private static List<string> returnedList = new List<string>();
        //
        private static string AuthToken = "sfh907JfjS98EROy8Bnvm7hDBpw8FJnc";
        public RestModule() : base("rest")
        {
            //First check for Authentication key
             Before += ctx =>
             {
                 var token = Request.Headers.Authorization;
                 if (!token.Equals(AuthToken))
                 {
                     Console.WriteLine("Auth Token Validation Error");
                     return Response.AsText("Auth Token Validation Error").WithStatusCode(403);
                 }
                 return null;
             };

            //Test
            #region test
            Get["/test"] = parameters =>
            {
                Console.WriteLine("\nTest from client.");
                return "TEST";
            };
            #endregion

            //Get last temperature value measured
            #region getTemperatureValue
            Get["/getstate/{deviceId}/temperature"] = parameters =>
            {
                string deviceId = parameters.deviceId;

                foreach (var device in AzureDevices.ToArray())
                {
                    if (deviceId == device.deviceId)
                    {
                        Console.WriteLine("\nSuccessfully returned temperature value of the IoTDevice with deviceId: " + deviceId + ".");
                        return JsonConvert.SerializeObject(new Value(device.deviceId, device.temperature));
                    }
                    else
                    {
                        if (device == AzureDevices[AzureDevices.Count - 1])
                        {
                            return "IoTDevice not found.";
                        }
                    }
                }

                return HttpStatusCode.OK;
            };
            #endregion

            //Get last humidity value measured
            #region getHumidityValue
            Get["/getstate/{deviceId}/humidity"] = parameters =>
            {
                string deviceId = parameters.deviceId;

                foreach (var device in AzureDevices.ToArray())
                {
                    if (deviceId == device.deviceId)
                    {
                        Console.WriteLine("\nSuccessfully returned humidity value of the IoTDevice with deviceId: " + deviceId + ".");
                        return JsonConvert.SerializeObject(new Value(device.deviceId, device.humidity));
                    }
                    else
                    {
                        if (device == AzureDevices[AzureDevices.Count - 1])
                        {
                            return "IoTDevice not found.";
                        }
                    }
                }

                return HttpStatusCode.OK;
            };
            #endregion

            //Get all temperature values
            #region getAllTemperatureValues
            Get["/getstate/{deviceId}/temperature/all"] = parameters =>
            {
                string deviceId = parameters.deviceId;

                foreach (var device in AzureDevices.ToArray())
                {
                    if (deviceId == device.deviceId)
                    {
                        returnedList.Clear();
                        foreach (var savedLog in TemperatureValues.ToArray())
                        {
                            if (deviceId == savedLog.deviceId)
                            {
                                returnedList.Add(savedLog.State);
                            }
                        }
                        Console.WriteLine("\nSuccessfully returned all Temperature Values of the IoTDevice with deviceId: " + deviceId + ".");
                        var templist = JsonConvert.SerializeObject(returnedList);
                        return templist;
                    }
                    else
                    {
                        if (device == AzureDevices[AzureDevices.Count - 1])
                        {
                            return "IoTDevice not found.";
                        }
                    }
                }

                return HttpStatusCode.OK;
            };
            #endregion

            //Get all humidity values
            #region getAllHumidityValues
            Get["/getstate/{deviceId}/humidity/all"] = parameters =>
            {
                string deviceId = parameters.deviceId;

                foreach (var device in AzureDevices.ToArray())
                {
                    if (deviceId == device.deviceId)
                    {
                        returnedList.Clear();
                        foreach (var savedLog in HumidityValues.ToArray())
                        {
                            if (deviceId == savedLog.deviceId)
                            {
                                returnedList.Add(savedLog.State);
                            }
                        }
                        Console.WriteLine("\nSuccessfully returned all Humidity Values of the IoTDevice with deviceId: " + deviceId + ".");
                        var humlist = JsonConvert.SerializeObject(returnedList);
                        return humlist;
                    }
                    else
                    {
                        if (device == AzureDevices[AzureDevices.Count - 1])
                        {
                            return "IoTDevice not found.";
                        }
                    }
                }

                return HttpStatusCode.OK;
            };
            #endregion

            //Get client devices
            #region getConectedClientDevices
            Get["/get/devices"] = parameters =>
            {
                if (AzureDevices.Count==0)
                {
                    Console.WriteLine("/nNo Client Devices connected found.");
                    return "No Client Devices connected found.";
                }
                else
                {
                    var devicelist = JsonConvert.SerializeObject(AzureDevices);
                    Console.WriteLine("\nSuccessfully returned all Connected Devices.");
                    return devicelist;
                }
            };
            #endregion

        }
    }
}
