package com.kairos.scheduler.kafka.producer;


import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.dto.scheduler.queue.KairosSchedulerLogsDTO;
import com.kairos.enums.scheduler.Result;
import com.kairos.scheduler.custom_exception.InvalidJobSubTypeException;
import com.kairos.scheduler.service.scheduler_panel.SchedulerPanelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.inject.Inject;

import static com.kairos.scheduler.constants.AppConstants.*;

@Component
public class KafkaProducer {


    @Inject
    private KafkaTemplate<Integer, KairosSchedulerExecutorDTO> kafkaTemplate;
    @Inject
    private SchedulerPanelService schedulerPanelService;


    private static Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public void pushToQueue(KairosSchedulerExecutorDTO job) {
        logger.info("Pushing to ScheduleTOUserQueue----------->" + job.getId());
        String queueLabel;
        if(userSubTypes.contains(job.getJobSubType())) {
            queueLabel = SCHEDULER_TO_USER_QUEUE_TOPIC;
        }
        else if(activitySubTypes.contains(job.getJobSubType())) {
            queueLabel = SCHEDULER_TO_ACTIVITY_QUEUE_TOPIC;
        }
        else {
            throw new InvalidJobSubTypeException("Invalid jobSubType");
        }
        KairosSchedulerLogsDTO schedulerLog;
        ListenableFuture<SendResult<Integer,KairosSchedulerExecutorDTO>> future =  kafkaTemplate.send(queueLabel, job);
        future.addCallback(new ListenableFutureCallback<SendResult<Integer, KairosSchedulerExecutorDTO>>() {

            @Override
            public void onSuccess(final SendResult<Integer, KairosSchedulerExecutorDTO> message) {
                logger.info("sent message= " + message + " with offset= " + message.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(final Throwable throwable) {
                logger.error("unable to send message= " + throwable.getMessage());
                KairosSchedulerLogsDTO schedulerLog = new KairosSchedulerLogsDTO(Result.ERROR,"unable to send message= " + throwable.getMessage(),job.getId(),job.getUnitId(),job.getJobSubType());
                schedulerPanelService.createJobScheduleDetails(schedulerLog);

            }
        });
    }

    /*public void pushToActivityQueue(KairosSchedulerExecutorDTO job) {

        logger.info("Pushing to Activity q {}  of type {}" + job.getId(), job.getJobSubType());
        kafkaTemplate.send(SCHEDULER_TO_ACTIVITY_QUEUE_TOPIC, job);

    }*/
   /* public KafkaMessageListenerContainer<Integer, QueueDTO> kafkaContainer() {
        ContainerProperties containerProps = new ContainerProperties("userSchedulerQueue");
        containerProps.setMessageListener( new DefaultKafkaMessageListener());

        Map<String, Object> props = consumerProps();
        DefaultKafkaConsumerFactory<Integer, QueueDTO> cf = new DefaultKafkaConsumerFactory<Integer, QueueDTO>(props);
        KafkaMessageListenerContainer<Integer, QueueDTO> container =
                new KafkaMessageListenerContainer<>(cf, containerProps);
        container.start();

        return container;
    }*/

    /*private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }
    private Map<String, Object> consumerProps() {
        Map<String, Objerect> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "groupId");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }*/
}
