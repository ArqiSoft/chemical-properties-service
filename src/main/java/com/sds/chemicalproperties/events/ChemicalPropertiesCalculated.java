package com.sds.chemicalproperties.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sds.chemicalproperties.model.CalculatedProperties;
import sds.messaging.contracts.AbstractContract;

import java.util.UUID;

public class ChemicalPropertiesCalculated extends AbstractContract {

    protected UUID id;
    protected UUID userId;
    protected String timeStamp;
    protected CalculatedProperties result;

    public ChemicalPropertiesCalculated() {
        namespace = "Sds.ChemicalProperties.Domain.Events";
        contractName = ChemicalPropertiesCalculated.class.getSimpleName();

    }

    public ChemicalPropertiesCalculated(UUID id, UUID userId, String timeStamp, CalculatedProperties result) {
        this.id = id;
        this.userId = userId;
        this.timeStamp = timeStamp;
        this.result = result;

        namespace = "Sds.ChemicalProperties.Domain.Events";
        contractName = ChemicalPropertiesCalculated.class.getSimpleName();
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

    @JsonProperty("Result")
    public CalculatedProperties getResult() {
        return result;
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

    public void setResult(CalculatedProperties result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ChemicalPropertiesCalculated{"
                + "id=" + id
                + ", userId=" + userId
                + ", timeStamp='" + timeStamp + '\''
                + ", result=" + result
                + '}';
    }
}
