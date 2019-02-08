using MassTransit;
using Sds.ChemicalProperties.Domain.Commands;
using System;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace Sds.ChemicalProperties.LoadTests
{
    public static class ChemicalPropertiesTestExtensions
    {
        public static async Task<Guid> UploadResource(this ChemicalPropertiesTestHarness harness, string bucket, string fileName)
        {
            return await UploadFile(harness, bucket, Path.Combine(Directory.GetCurrentDirectory(), "Resources", fileName));
        }

        public static async Task<Guid> UploadFile(this ChemicalPropertiesTestHarness harness, string bucket, string path)
        {
            var source = new FileStream(path, FileMode.Open, FileAccess.Read);
            return await harness.BlobStorage.AddFileAsync(Path.GetFileName(path), source, "application/octet-stream", bucket);
        }

        public static async Task PublishCalculateChemicalProperties(this ChemicalPropertiesTestHarness harness, Guid id, Guid blobId, string bucket, Guid userId, Guid correlationId)
        {
            await harness.BusControl.Publish<CalculateChemicalProperties>(new
            {
                Id = id,
                UserId = userId,
                BlobId = blobId,
                Bucket = bucket,
                CorrelationId = correlationId
            });
        }

        public static async Task CalculateChemicalProperties(this ChemicalPropertiesTestHarness harness, Guid id, Guid blobId, string bucket, Guid userId, Guid correlationId)
        {
            await harness.PublishCalculateChemicalProperties(id, blobId, bucket, userId, correlationId);

            harness.WaitWhileProcessingFinished(correlationId);
        }

        public static void WaitWhileProcessingFinished(this ChemicalPropertiesTestHarness harness, Guid correlationId)
        {
            if (!harness.Received.Select<CorrelatedBy<Guid>>(m => m.Context.Message.CorrelationId == correlationId).Any())
            {
                throw new TimeoutException();
            }
        }
    }
}
