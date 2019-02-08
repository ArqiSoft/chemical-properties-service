using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;
using Xunit;
using Xunit.Abstractions;

namespace Sds.ChemicalProperties.LoadTests {
    [Collection ("ChemicalProperties Test Harness")]
    public class BulkOfValidFilesTest : ChemicalPropertiesTest {
        public BulkOfValidFilesTest (ChemicalPropertiesTestHarness harness, ITestOutputHelper output) : base (harness, output) {
            this.output = output;
        }

        private readonly ITestOutputHelper output;

        public class IEvent {
            public Guid id;
            public Guid correlationId;

            public IEvent(Guid id, Guid correlationId)
            {
                this.id = id;
                this.correlationId = correlationId;
            }
        };

        [Fact]
        public async Task LoadTesting_BulkOfValidFiles_ProcessedSuccessfully () {
            // var correlations = new List<Guid> ();
            // var ids = new List<Guid> ();

            List<IEvent> data = new List<IEvent> ();

            // string[] files = Directory.GetFiles(@"Resources", "*.*");
            string[] file = Directory.GetFiles (@"Resources", "Aspirin.mol");

            for (var i = 1; i <= 1000; i++) {
                var Id = Guid.NewGuid ();
                var bucket = i.ToString ();
                var userId = Guid.NewGuid ();
                var blobId = await Harness.UploadFile (bucket, file[0]);
                var correlationId = Guid.NewGuid ();

                await Harness.PublishCalculateChemicalProperties (Id, blobId, bucket, userId, correlationId);

                data.Add(new IEvent(Id, correlationId));
                // correlations.Add (correlationId);
            }

            foreach (IEvent item in data) {
                Harness.WaitWhileProcessingFinished (item.correlationId);
                // var evn = Harness.GetChemicalPropertiesCalculatedEvent (item.id);

            }
            // foreach (var correlationId in correlations) {
            //     Harness.WaitWhileProcessingFinished (correlationId);
            // }
        }
    }
}