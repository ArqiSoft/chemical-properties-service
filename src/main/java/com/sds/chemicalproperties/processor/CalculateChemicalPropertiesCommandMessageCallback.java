package com.sds.chemicalproperties.processor;

import java.util.concurrent.BlockingQueue;

import com.sds.chemicalproperties.commands.CalculateChemicalProperties;
import sds.messaging.callback.AbstractMessageCallback;

public class CalculateChemicalPropertiesCommandMessageCallback extends AbstractMessageCallback<CalculateChemicalProperties> {

    public CalculateChemicalPropertiesCommandMessageCallback(Class<CalculateChemicalProperties> tClass, BlockingQueue<CalculateChemicalProperties> queue) {
        super(tClass, queue);
    }

}
