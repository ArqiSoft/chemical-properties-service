package com.sds.chemicalproperties.config;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sds.chemicalproperties.commands.CalculateChemicalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.consumer.ReceiverBusControl;
import com.npspot.jtransitlight.consumer.setting.ConsumerSettings;
import com.npspot.jtransitlight.publisher.IBusControl;
import com.sds.chemicalproperties.processor.CalculateChemicalPropertiesCommandMessageCallback;
import sds.messaging.callback.MessageProcessor;

@Component
public class MessageProcessorConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessorConfiguration.class);

    
    @Autowired
    public MessageProcessorConfiguration(IBusControl busControl, 
            ReceiverBusControl receiver, 
            MessageProcessor<CalculateChemicalProperties> processor,
            BlockingQueue<CalculateChemicalProperties> queue,
            // TODO: Define queue name in application.properties
            @Value("${queueName}") String queueName,
            @Value("${EXECUTOR_THREAD_COUNT:5}") Integer threadCount) 
                    throws JTransitLightException, IOException, InterruptedException {
        
        receiver.subscribe(new CalculateChemicalProperties().getQueueName(), queueName,
                ConsumerSettings.newBuilder().withDurable(true).build(), 
                new CalculateChemicalPropertiesCommandMessageCallback(CalculateChemicalProperties.class, queue));
        
        LOGGER.debug("EXECUTOR_THREAD_COUNT is set to {}", threadCount);
        
        
        Executors.newSingleThreadExecutor().submit(() -> {
            final ExecutorService threadPool = 
                    Executors.newFixedThreadPool(threadCount);
            
            while (true) {
                // wait for message
                final CalculateChemicalProperties message = queue.take();
                
                // submit to processing pool
                threadPool.submit(() -> processor.process(message));
                Thread.sleep(10);
            }
        });
    }
    
}
