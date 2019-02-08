using MassTransit;
using System;

namespace Sds.ChemicalProperties.Domain.Events
{
    public interface ChemicalPropertiesCalculationFailed : CorrelatedBy<Guid>
    {
        Guid Id { get; }
        Guid UserId { get; }
        DateTimeOffset TimeStamp { get; }
        String CalculationException { get; }
    }
}
