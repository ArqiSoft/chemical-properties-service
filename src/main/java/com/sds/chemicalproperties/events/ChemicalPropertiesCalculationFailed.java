package com.sds.chemicalproperties.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import sds.messaging.contracts.AbstractContract;

import java.util.UUID;

public class ChemicalPropertiesCalculationFailed extends AbstractContract {
    protected UUID id;
    protected UUID userId;
    protected String timeStamp;
    protected Exception calculationException;

    public ChemicalPropertiesCalculationFailed() {
        namespace = "Sds.ChemicalProperties.Domain.Events";
        contractName = ChemicalPropertiesCalculationFailed.class.getSimpleName();
    }

    public ChemicalPropertiesCalculationFailed(UUID id, UUID userId, String timeStamp, Exception calculationException) {
        this.id = id;
        this.userId = userId;
        this.timeStamp = timeStamp;
        this.calculationException = calculationException;

        namespace = "Sds.ChemicalProperties.Domain.Events";
        contractName = ChemicalPropertiesCalculationFailed.class.getSimpleName();
    }

    @JsonProperty("Id")
    public UUID getId() {
        return id;
    }

    @JsonProperty("UserId")
    public UUID getUserId() {
        return userId;
    }

    @JsonProperty("TimeStamp")
    public String getTimeStamp() {
        return timeStamp;
    }

    @JsonProperty("CalculationException")
    public Exception getCalculationException() {
        return calculationException;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setCalculationException(Exception calculationException) {
        this.calculationException = calculationException;
    }

    @Override
    public String toString() {
        return "ChemicalPropertiesCalculationFailed{" +
                "id=" + id +
                ", userId=" + userId +
                ", timeStamp='" + timeStamp + '\'' +
                ", calculationException=" + calculationException +
                '}';
    }
}
