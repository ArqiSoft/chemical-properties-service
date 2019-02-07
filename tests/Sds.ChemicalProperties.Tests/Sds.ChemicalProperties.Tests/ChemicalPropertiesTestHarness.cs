using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using MassTransit;
using MassTransit.ExtensionsDependencyInjectionIntegration;
using MassTransit.RabbitMqTransport;
using MassTransit.Scoping;
using MassTransit.Testing.MessageObservers;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using MongoDB.Driver;
using Sds.ChemicalProperties.Domain.Events;
using Sds.MassTransit.RabbitMq;
using Sds.Storage.Blob.Core;
using Sds.Storage.Blob.GridFs;
using Serilog;

namespace Sds.ChemicalProperties.Tests {
    public class ChemicalPropertiesTestHarness : IDisposable {
        protected IServiceProvider _serviceProvider;

        public IBlobStorage BlobStorage { get { return _serviceProvider.GetService<IBlobStorage> (); } }

        public IBusControl BusControl { get { return _serviceProvider.GetService<IBusControl> (); } }

        private List<ExceptionInfo> Faults = new List<ExceptionInfo> ();
        public ReceivedMessageList Received { get; } = new ReceivedMessageList (TimeSpan.FromSeconds (10));

        public ChemicalPropertiesTestHarness () {
            var configuration = new ConfigurationBuilder ()
                .AddJsonFile ("appsettings.json", true, true)
                .AddEnvironmentVariables ()
                .Build ();

            Log.Logger = new LoggerConfiguration ()
                .CreateLogger ();

            Log.Information ("Staring ChemicalProperties tests");

            var services = new ServiceCollection ();

            services.AddTransient<IBlobStorage, GridFsStorage> (x => {
                var blobStorageUrl = new MongoUrl (Environment.ExpandEnvironmentVariables (configuration["GridFs:ConnectionString"]));
                var client = new MongoClient (blobStorageUrl);

                return new GridFsStorage (client.GetDatabase (blobStorageUrl.DatabaseName));
            });

            services.AddSingleton<IConsumerScopeProvider, DependencyInjectionConsumerScopeProvider> ();

            services.AddSingleton (container => Bus.Factory.CreateUsingRabbitMq (x => {
                IRabbitMqHost host = x.Host (new Uri (Environment.ExpandEnvironmentVariables (configuration["MassTransit:ConnectionString"])), h => { });

                x.RegisterConsumers (host, container, e => {
                    e.UseInMemoryOutbox ();
                });

                x.ReceiveEndpoint (host, "processing_fault_queue", e => {
                    e.Handler<Fault> (async context => {
                        Faults.AddRange (context.Message.Exceptions.Where (ex => !ex.ExceptionType.Equals ("System.InvalidOperationException")));

                        await Task.CompletedTask;
                    });
                });

                x.ReceiveEndpoint (host, "processing_update_queue", e => {
                    e.Handler<ChemicalPropertiesCalculated> (context => { Received.Add (context); return Task.CompletedTask; });
                    e.Handler<ChemicalPropertiesCalculationFailed> (context => { Received.Add (context); return Task.CompletedTask; });
                });
            }));

            _serviceProvider = services.BuildServiceProvider ();

            var busControl = _serviceProvider.GetRequiredService<IBusControl> ();

            busControl.Start ();
        }

        public ChemicalPropertiesCalculated GetChemicalPropertiesCalculatedEvent (Guid id) {
            return Received
                .ToList ()
                .Where (m => m.Context.GetType ().IsGenericType && m.Context.GetType ().GetGenericArguments () [0] == typeof (ChemicalPropertiesCalculated))
                .Select (m => (m.Context as ConsumeContext<ChemicalPropertiesCalculated>).Message)
                .Where (m => m.Id == id).ToList ().SingleOrDefault ();
        }

        public ChemicalPropertiesCalculationFailed GetChemicalPropertiesCalculationFailedEvent (Guid id) {
            return Received
                .ToList ()
                .Where (m => m.Context.GetType ().IsGenericType && m.Context.GetType ().GetGenericArguments () [0] == typeof (ChemicalPropertiesCalculationFailed))
                .Select (m => (m.Context as ConsumeContext<ChemicalPropertiesCalculationFailed>).Message)
                .Where (m => m.Id == id).ToList ().SingleOrDefault ();
        }

        public virtual void Dispose () {
            var busControl = _serviceProvider.GetRequiredService<IBusControl> ();
            busControl.Stop ();
        }
    }
}