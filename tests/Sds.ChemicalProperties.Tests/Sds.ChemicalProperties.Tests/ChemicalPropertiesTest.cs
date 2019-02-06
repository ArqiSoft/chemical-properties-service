using MassTransit;
using Sds.Storage.Blob.Core;
using Serilog;
using Serilog.Events;
using Xunit;
using Xunit.Abstractions;

namespace Sds.ChemicalProperties.Tests
{
    [CollectionDefinition("ChemicalProperties Test Harness")]
    public class OsdrTestCollection : ICollectionFixture<ChemicalPropertiesTestHarness>
    {
    }

    public abstract class ChemicalPropertiesTest
    {
        public ChemicalPropertiesTestHarness Harness { get; }

        protected IBus Bus => Harness.BusControl;
        protected IBlobStorage BlobStorage => Harness.BlobStorage;

        public ChemicalPropertiesTest(ChemicalPropertiesTestHarness fixture, ITestOutputHelper output = null)
        {
            Harness = fixture;

            if (output != null)
            {
                Log.Logger = new LoggerConfiguration()
                    .MinimumLevel.Debug()
                    .WriteTo
                    .TestOutput(output, LogEventLevel.Verbose)
                    .CreateLogger()
                    .ForContext<ChemicalPropertiesTest>();
            }
        }
    }
}
