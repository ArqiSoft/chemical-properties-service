using System;
using System.Linq;
using System.Threading.Tasks;
using FluentAssertions;
using FluentAssertions.Extensions;
using Xunit;
using Xunit.Abstractions;

namespace Sds.ChemicalProperties.Tests {
    public class InvalidMolFileTestFixture {
        public Guid UserId { get; } = Guid.NewGuid ();
        public Guid BlobId { get; }
        public string Bucket { get; }
        public Guid Id { get; } = Guid.NewGuid ();
        public Guid CorrelationId { get; } = Guid.NewGuid ();

        public InvalidMolFileTestFixture (ChemicalPropertiesTestHarness harness) {
            Bucket = UserId.ToString ();
            BlobId = harness.UploadResource (Bucket, "empty.rxn").Result;
            harness.CalculateChemicalProperties (Id, BlobId, Bucket, UserId, CorrelationId).Wait ();
        }
    }

    [Collection ("ChemicalProperties Test Harness")]
    public class InvalidMolFileTest : ChemicalPropertiesTest, IClassFixture<InvalidMolFileTestFixture> {
        private Guid CorrelationId;
        private string Bucket;
        private Guid UserId;
        private Guid Id;

        public InvalidMolFileTest(ChemicalPropertiesTestHarness harness, ITestOutputHelper output, InvalidMolFileTestFixture initFixture) : base(harness, output)
        {
            Id = initFixture.Id;
            CorrelationId = initFixture.CorrelationId;
            Bucket = initFixture.Bucket;
            UserId = initFixture.UserId;
        }

        [Fact]
        public void ChemicalPropertiesCalculation_ValidMolFile_ShouldFailToCalculateProperties()
        {
           var evn = Harness.GetChemicalPropertiesCalculationFailedEvent(Id);
           evn.Should().NotBeNull();
           evn.UserId.Should().Be(UserId);
           evn.CorrelationId.Should().Be(CorrelationId);
           evn.TimeStamp.Should().BeBefore(DateTime.Now).And.BeLessThan(2000.Milliseconds());
           evn.CalculationException.Should().NotBeNull();
        }
    }
}