package com.kairos.kafka;

import com.kairos.dto.QueueDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

public class KafkaUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public KafkaMessageListenerContainer<Integer,QueueDTO> createContainer(String topic) {

        ContainerProperties containerProps = new ContainerProperties(topic);
        containerProps.setMessageListener(new MessageListener<Integer,QueueDTO>() {

            public void onMessage(ConsumerRecord<Integer,QueueDTO> message) {
                logger.info("received: "+ message);
            }
        });

        Map<String, Object> props = consumerProps();

        DefaultKafkaConsumerFactory<Integer, QueueDTO> cf = new DefaultKafkaConsumerFactory<Integer, QueueDTO>(props);
        KafkaMessageListenerContainer<Integer, QueueDTO> container = new KafkaMessageListenerContainer<>(cf, containerProps);

        return container;

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
    }
}
