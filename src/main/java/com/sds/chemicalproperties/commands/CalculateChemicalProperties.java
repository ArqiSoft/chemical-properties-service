package com.sds.chemicalproperties.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import sds.messaging.contracts.AbstractContract;

import java.util.UUID;

public class CalculateChemicalProperties extends AbstractContract {
    protected UUID id;
    protected String bucket;
    protected UUID blobId;
    protected UUID userId;

    public CalculateChemicalProperties() {
        namespace = "Sds.ChemicalProperties.Domain.Commands";
        contractName = CalculateChemicalProperties.class.getSimpleName();
    }

    public CalculateChemicalProperties(UUID id, String bucket, UUID blobId, UUID userId) {
        this.id = id;
        this.bucket = bucket;
        this.blobId = blobId;
        this.userId = userId;

        namespace = "Sds.ChemicalProperties.Domain.Commands";
        contractName = CalculateChemicalProperties.class.getSimpleName();
    }

    @JsonProperty("Id")
    public UUID getId() {
        return id;
    }

    @JsonProperty("Bucket")
    public String getBucket() {
        return bucket;
    }

    @JsonProperty("BlobId")
    public UUID getBlobId() {
        return blobId;
    }

    @JsonProperty("UserId")
    public UUID getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "CalculateChemicalProperties{" +
                "id=" + id +
                ", bucket='" + bucket + '\'' +
                ", blobId=" + blobId +
                ", userId=" + userId +
                ", namespace='" + namespace + '\'' +
                ", contractName='" + contractName + '\'' +
                ", correlationId=" + getCorrelationId() +
                '}';
    }
}
