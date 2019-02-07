using System;
using System.Linq;
using System.Threading.Tasks;
using FluentAssertions;
using Xunit;
using Xunit.Abstractions;

namespace Sds.ChemicalProperties.Tests {
    public class MolFileTestFixture {
        public Guid UserId { get; } = Guid.NewGuid ();
        public Guid BlobId { get; }
        public string Bucket { get; }
        public Guid Id { get; } = Guid.NewGuid ();
        public Guid CorrelationId { get; } = Guid.NewGuid ();

        public MolFileTestFixture (ChemicalPropertiesTestHarness harness) {
            Bucket = UserId.ToString ();
            BlobId = harness.UploadResource (Bucket, "chemspider.mol").Result;
            harness.CalculateChemicalProperties (Id, BlobId, Bucket, UserId, CorrelationId).Wait ();
        }
    }

    [Collection ("ChemicalProperties Test Harness")]
    public class MolFileTest : ChemicalPropertiesTest, IClassFixture<MolFileTestFixture> {
        private Guid CorrelationId;
        private string Bucket;
        private Guid UserId;
        private Guid Id;

        public MolFileTest (ChemicalPropertiesTestHarness harness, ITestOutputHelper output, MolFileTestFixture initFixture) : base (harness, output) {
            Id = initFixture.Id;
            CorrelationId = initFixture.CorrelationId;
            Bucket = initFixture.Bucket;
            UserId = initFixture.UserId;
        }

        [Fact]
        public void ChemicalPropertiesCalculation_ValidMolFile_ShouldCalculateProperties () {
            var evn = Harness.GetChemicalPropertiesCalculatedEvent (Id);
            evn.Result.Properties.Should ().NotBeEmpty ();
            evn.Result.Issues.Should ().NotBeEmpty ();
        }
    }
}