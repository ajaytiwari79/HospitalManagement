package com.kairos.config.web_socket;

import com.kairos.constants.ApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.sockjs.SockJsService;

@Configuration
@EnableWebSocketMessageBroker
@Controller
@EnableWebSocket
/**
 * An abstract base class for {@link SockJsService},{@link AbstractSockJsService} implementations that provides SockJS
 * path resolution and handling of static SockJS requests (e.g. "/info", "/iframe.html",
 * etc). Sub-classes must handle session URLs (i.e. transport-specific requests).
 *
 * By default, only same origin requests are allowed. Use {@link #setAllowedOrigins}
 * to specify a list of allowed origins (a list containing "*" will allow all origins).
 *
 * @author pradeep singh kushwah
 * @date 24-01-2020
 */
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(ApiConstants.API_V1 + "/ws/dynamic-push");//.setTaskScheduler(getThreadPoolTaskScheduler()).setHeartbeatValue(new long[]{10000, 10000});
        config.setApplicationDestinationPrefixes(ApiConstants.API_V1 + "/ws");
    }

    /*public ThreadPoolTaskScheduler getThreadPoolTaskScheduler() {
        ThreadPoolTaskScheduler te = new ThreadPoolTaskScheduler();
        te.setPoolSize(1);
        te.setThreadNamePrefix("wss-heartbeat-thread-");
        te.setWaitForTasksToCompleteOnShutdown(true);
        te.initialize();
        return te;
    }*/

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/v1/ws")
                 .setAllowedOrigins("*").withSockJS();
    }

    @Bean
    public WebSocketHeaderInterceptor webSocketHeaderInterceptor() {
        return new WebSocketHeaderInterceptor();
    }
}