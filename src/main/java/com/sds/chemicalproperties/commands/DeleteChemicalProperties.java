package com.sds.chemicalproperties.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import sds.messaging.contracts.AbstractContract;

import java.util.UUID;

public class DeleteChemicalProperties  extends AbstractContract {
    protected UUID id;
    protected UUID userId;

    public DeleteChemicalProperties() {
    }

    public DeleteChemicalProperties(UUID id, UUID userId) {
        this.id = id;
        this.userId = userId;

        namespace = "Sds.ChemicalProperties.Domain.Commands";
        contractName = DeleteChemicalProperties.class.getSimpleName();
    }

    @JsonProperty("Id")
    public UUID getId() {
        return id;
    }

    @JsonProperty("UserId")
    public UUID getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "DeleteChemicalProperties{" +
                "id=" + id +
                ", userId=" + userId +
                ", namespace='" + namespace + '\'' +
                ", contractName='" + contractName + '\'' +
                ", correlationId=" + getCorrelationId() +
                '}';
    }
}
