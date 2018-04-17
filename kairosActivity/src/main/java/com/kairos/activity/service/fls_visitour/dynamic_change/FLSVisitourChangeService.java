package com.kairos.activity.service.fls_visitour.dynamic_change;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
/*import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;*/
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.concurrent.CountDownLatch;
/**
 * Created by neuron on 8/5/17.
 */
@Service
public class FLSVisitourChangeService {

    private static final Logger logger = LoggerFactory.getLogger(FLSVisitourChangeService.class);

    @Inject
    private ApplicationContext applicationContext;


    public void registerReceiver(String topic){
        /*RedisMessageListenerContainer container = applicationContext.getBean(RedisMessageListenerContainer.class);
        MessageListenerAdapter listenerAdapter =  applicationContext.getBean(MessageListenerAdapter.class);
        container.addMessageListener(listenerAdapter, new PatternTopic(topic));
        StringRedisTemplate template = applicationContext.getBean(StringRedisTemplate.class);*/

    }



    public void pushToQueue(String queue,String message) throws InterruptedException {
        /*StringRedisTemplate template = applicationContext.getBean(StringRedisTemplate.class);

        CountDownLatch latch = applicationContext.getBean(CountDownLatch.class);

        logger.info("Sending message...");
        template.convertAndSend(queue, message);
        latch.await();*/
    }

}
