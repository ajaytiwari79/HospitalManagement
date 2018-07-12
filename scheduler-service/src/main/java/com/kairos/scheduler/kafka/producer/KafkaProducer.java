package com.kairos.scheduler.kafka.producer;


import com.kairos.dto.KairosScheduleJobDTO;
import com.kairos.dto.KairosSchedulerExecutorDTO;
import com.kairos.dto.QueueDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaProducer {


    @Inject
    private KafkaTemplate<Integer,KairosSchedulerExecutorDTO> kafkaTemplate;

    public void pushToQueue(KairosSchedulerExecutorDTO job) {

      kafkaTemplate.send("SchedulerToUserQueue",1,job);
    }

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
        Map<String, Object> props = new HashMap<>();
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
