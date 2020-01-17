package com.kairos.config.web_socket;

import com.kairos.constants.ApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        ThreadPoolTaskScheduler te = new ThreadPoolTaskScheduler();
        te.setPoolSize(1);
        te.setThreadNamePrefix("wss-heartbeat-thread-");
        te.setWaitForTasksToCompleteOnShutdown(true);
        te.initialize();
        config.enableSimpleBroker(ApiConstants.API_V1+"/ws/dynamic-push")
        .setTaskScheduler(te)
        .setHeartbeatValue(new long[]{10000,10000});
        config.setApplicationDestinationPrefixes(ApiConstants.API_V1+"/ws");

           }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/v1/ws")
                 .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHeaderInterceptor webSocketHeaderInterceptor() {
        return new WebSocketHeaderInterceptor();
    }
}