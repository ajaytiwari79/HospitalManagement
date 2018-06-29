package com.kairos.config.web_socket;

import com.kairos.config.security.WebSocketHeaderInterceptor;
import com.kairos.constants.ApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(ApiConstants.API_V1+"/kairos/ws/dynamic-push");
        config.setApplicationDestinationPrefixes(ApiConstants.API_V1+"/kairos/ws");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(ApiConstants.API_V1+"/kairos/ws").setAllowedOrigins("*").withSockJS();
    }

    @Bean
    public WebSocketHeaderInterceptor webSocketHeaderInterceptor() {
        return new WebSocketHeaderInterceptor();
    }
}