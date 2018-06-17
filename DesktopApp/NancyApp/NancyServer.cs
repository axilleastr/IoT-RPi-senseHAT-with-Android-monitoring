using System;
using System.Threading;
using Nancy.Hosting.Self;

namespace NancySample
{
    public class NancyServer
    {
        public NancyServer()
        {
            var uri = new Uri("http://localhost:6202");

            HostConfiguration hostConfigs = new HostConfiguration();
            hostConfigs.UrlReservations.CreateAutomatically = true;
            using (var host = new NancyHost(hostConfigs, uri))
            {
                host.Start();
                Console.WriteLine("Your application is running on " + uri);

                while (true) Thread.Sleep(1000); //kill me..

            }
        }
    }
}
