package com.sds.chemicalproperties.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import sds.messaging.contracts.AbstractContract;

import java.util.UUID;

public class ChemicalPropertiesCalculationPersisted extends AbstractContract {
    protected UUID id;
    protected UUID userId;
    protected String timeStamp;

    public ChemicalPropertiesCalculationPersisted() {
    }

    public ChemicalPropertiesCalculationPersisted(UUID id, UUID userId, String timeStamp) {
        this.id = id;
        this.userId = userId;
        this.timeStamp = timeStamp;

        namespace = "Sds.ChemicalProperties.Domain.Events";
        contractName = ChemicalPropertiesCalculationPersisted.class.getSimpleName();
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

    @Override
    public String toString() {
        return "ChemicalPropertiesCalculationPersisted{" +
                "id=" + id +
                ", userId=" + userId +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
