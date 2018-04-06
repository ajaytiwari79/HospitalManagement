package com.kairos.activity.messaging;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.kairos.activity.persistence.model.task.Task;
import com.kairos.activity.service.task_type.TaskDynamicReportService;
import com.kairos.activity.service.task_type.TaskService;
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
    TaskService taskService;
    @Inject
    TaskDynamicReportService taskDynamicReportService;

    @Inject
    public Receiver(CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(String message) throws IOException {

        LOGGER.debug("Received <" + message + ">");
        XmlMapper xmlMapper = new XmlMapper();
        message = message.substring(message.indexOf("<DynamicChange"),message.indexOf("</DynamicChange>")+16);
        LOGGER.debug("parsing now"+message);
        Task task = null;
        try{

            ReceivedTask receivedTask = xmlMapper.readValue(message,ReceivedTask.class);
            task = taskService.updateTaskStatus(receivedTask);

        } catch (Exception exception){
            LOGGER.warn("Exception in Receiver ", exception);
        }finally {
            latch.countDown();
        }
        if(task != null){
            taskDynamicReportService.aggregateReports(task);
        }
     }
}

