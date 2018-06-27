package com.kairos.config.redis;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by neuron on 4/5/17.
 */
public class Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    private CountDownLatch latch;

    @Inject
    public Receiver(CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(String message) throws IOException {
        LOGGER.info("Received <" + message + ">");
        XmlMapper xmlMapper = new XmlMapper();
        message = message.substring(message.indexOf("<DynamicChange"),message.indexOf("</DynamicChange>")+16);
        System.out.println("parsing now"+message);

    }
}
