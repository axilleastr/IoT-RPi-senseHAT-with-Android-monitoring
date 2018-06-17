using System.Threading.Tasks;
using CloudInteractions;
using NancySample;
using static southbound_API.API;

namespace IoTProjectAppp
{
    public class MainClass
    {

        public static void Main(string[] args)
        {
            
            //Start Reading Device To Azure Cloud Messages
            Task.Run(() =>
            {
                ReadDeviceToAzureMessages rm = new ReadDeviceToAzureMessages(AzureDevices, TemperatureValues, HumidityValues);
            }
            );

            //Connect To IBM Client And Subscribe To Report Event
            Task.Run(() =>
            {
                IBMMessages ibmcl = new IBMMessages();
            }
            );

            //Start Rest API Module
            NancyServer ns = new NancyServer();

        }
    }
}