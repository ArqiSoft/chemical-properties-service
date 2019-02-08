package com.sds.chemicalproperties.processor;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import com.epam.indigo.Indigo;
import com.epam.indigo.IndigoInchi;
import com.epam.indigo.IndigoObject;
import com.sds.chemicalproperties.commands.CalculateChemicalProperties;
import com.sds.chemicalproperties.events.ChemicalPropertiesCalculated;
import com.sds.chemicalproperties.events.ChemicalPropertiesCalculationFailed;
import com.sds.chemicalproperties.model.CalculatedProperties;
import com.sds.domain.Property;
import com.sds.storage.BlobInfo;
import com.sds.storage.Guid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.npspot.jtransitlight.consumer.ReceiverBusControl;
import com.npspot.jtransitlight.publisher.IBusControl;
import com.sds.storage.BlobStorage;
import sds.messaging.callback.MessageProcessor;

@Component
public class CalculateChemicalPropertiesCommandProcessor implements MessageProcessor<CalculateChemicalProperties> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateChemicalPropertiesCommandProcessor.class);

    ReceiverBusControl receiver;
    IBusControl bus;
    BlobStorage storage;


    @Autowired
    public CalculateChemicalPropertiesCommandProcessor(ReceiverBusControl receiver, IBusControl bus, BlobStorage storage) {
        this.bus = bus;
        this.receiver = receiver;
        this.storage = storage;
    }

    @Override
    public void process(CalculateChemicalProperties message) {

        try {
            BlobInfo blob = storage.getFileInfo(new Guid(message.getBlobId()), message.getBucket());

            if (blob == null) {
                throw new FileNotFoundException(String.format("Blob with Id %s not found in bucket %s",
                        new Guid(message.getBlobId()), message.getBucket()));
            }

            InputStream inputStream = storage.getFileStream(new Guid(message.getBlobId()), message.getBucket());
            BufferedReader bfReader;
            StringBuilder mol = new StringBuilder();
            String line;

            bfReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bfReader.readLine()) != null) {
                mol.append(line).append("\n");
            }
            bfReader.close();

            Indigo indigo = new Indigo();
            indigo.setOption("ignore-stereochemistry-errors", "true");
            indigo.setOption("unique-dearomatization", "false");
            indigo.setOption("ignore-noncritical-query-features", "true");
            indigo.setOption("timeout", "600000");

            IndigoInchi indigo_inchi = new IndigoInchi(indigo);
            IndigoObject molecule = indigo.loadMolecule(mol.toString());
            String inchiString = indigo_inchi.getInchi(molecule);

            List<Property> properties = new ArrayList<>();

            getSmiles(molecule, properties);
            getMolecularFormula(molecule, properties);
            getMolecularWeight(molecule, properties);
            getMonoisotopicMass(molecule, properties);
            getMostAbundantMass(molecule, properties);
            getInChI(properties, inchiString);
            InChIKey(properties, inchiString, indigo_inchi);

            CalculatedProperties result = new CalculatedProperties(new ArrayList<>(), properties);
            publishSuccessEvent(message, result);

            indigo = null;

            LOGGER.debug("Properties calculated event has been issued.");
        } catch (Exception exception) {
            publishFailureEvent(message, exception.getMessage());
        }

    }

    private void getSmiles(IndigoObject molecule, List<Property> properties) {
        try {
            properties.add(new Property("SMILES", molecule.canonicalSmiles(), 0.));
        } catch (Exception e) {
            LOGGER.error("Failed get SMILES: {}", e.getMessage());
        }
    }

    private void getMolecularFormula(IndigoObject molecule, List<Property> properties) {
        try {
            properties.add(new Property("MOLECULAR_FORMULA", molecule.grossFormula(), 0.));
        } catch (Exception e) {
            LOGGER.error("Failed get MOLECULAR_FORMULA: {}", e.getMessage());
        }
    }

    private void getMolecularWeight(IndigoObject molecule, List<Property> properties) {
        try {
            properties.add(new Property("MOLECULAR_WEIGHT", String.valueOf(molecule.molecularWeight()), 0.));
        } catch (Exception e) {
            LOGGER.error("Failed to get MOLECULAR_WEIGHT: {}", e.getMessage());
        }
    }

    private void getMonoisotopicMass(IndigoObject molecule, List<Property> properties) {
        try {
            properties.add(new Property("MONOISOTOPIC_MASS", String.valueOf(molecule.monoisotopicMass()), 0.));
        } catch (Exception e) {
            LOGGER.error("Failed to get MONOISOTOPIC_MASS: {}", e.getMessage());
        }
    }

    private void getMostAbundantMass(IndigoObject molecule, List<Property> properties) {
        try {
            properties.add(new Property("MOST_ABUNDANT_MASS", String.valueOf(molecule.mostAbundantMass()), 0.));
        } catch (Exception e) {
            LOGGER.error("Failed to get MOST_ABUNDANT_MASS: {}", e.getMessage());
        }
    }

    private void getInChI(List<Property> properties, String inchiString) {
        try {
            properties.add(new Property("InChI", inchiString, 0.));
        } catch (Exception e) {
            LOGGER.error("Failed to get InChI: {}", e.getMessage());
        }
    }

    private void InChIKey(List<Property> properties, String inchiString, IndigoInchi indigo_inchi) {
        try {
            properties.add(new Property("InChIKey", indigo_inchi.getInchiKey(inchiString), 0.));
        } catch (Exception e) {
            LOGGER.error("Failed to get InChIKey: {}", e.getMessage());
        }
    }

    private void publishSuccessEvent(CalculateChemicalProperties message, CalculatedProperties result) {
        ChemicalPropertiesCalculated event = new ChemicalPropertiesCalculated();
        event.setId(message.getId());
        //event.setId(UUID.randomUUID());
        event.setUserId(message.getUserId());
        event.setTimeStamp(getTimestamp());
        event.setResult(result);
        event.setCorrelationId(message.getCorrelationId());

        LOGGER.debug("Publishing event {}", event);

        bus.publish(event);
    }

    private void publishFailureEvent(CalculateChemicalProperties message, String exception) {
        ChemicalPropertiesCalculationFailed event = new ChemicalPropertiesCalculationFailed();
        event.setId(message.getId());
        event.setUserId(message.getUserId());
        event.setTimeStamp(getTimestamp());
        event.setCorrelationId(message.getCorrelationId());
        event.setCalculationException(exception);

        LOGGER.debug("Publishing event {}", event);

        bus.publish(event);
    }

    private String getTimestamp() {
        //("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return LocalDateTime.now().toString();
    }


}
