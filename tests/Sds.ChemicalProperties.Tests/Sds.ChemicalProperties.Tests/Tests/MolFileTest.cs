using System;
using System.Linq;
using System.Threading.Tasks;
using FluentAssertions;
using Sds.Domain;
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
            evn.UserId.Should().Be(UserId);
            evn.CorrelationId.Should().Be(CorrelationId);
            evn.Result.Issues.Count().Should ().BeGreaterOrEqualTo (0);
            evn.Result.Properties.Should ().NotBeEmpty ();
            evn.Result.Properties.ToList().ForEach((Property prop) => {
                string[] nameList = {"SMILES", "MOLECULAR_FORMULA", "MOLECULAR_WEIGHT", "MONOISOTOPIC_MASS", "MOST_ABUNDANT_MASS", "InChI", "InChIKey"};
                string[] valueList = {"OC1=CC=C(C=C1)C=O", "C7 H6 O2", "122.12134218215942", "122.03678011894226", "122.03678011894226", "InChI=1S/C7H6O2/c8-5-6-1-3-7(9)4-2-6/h1-5,9H", "RGHHSNMVTDWUBI-UHFFFAOYSA-N"};
                prop.Name.Should().BeOneOf(nameList);
                prop.Value.ToString().Should().BeOneOf(valueList);
            });
        }
    }
}