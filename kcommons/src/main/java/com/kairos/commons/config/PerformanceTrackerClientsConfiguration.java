package com.kairos.commons.config;


import com.mindscapehq.raygun4java.core.RaygunClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
public class PerformanceTrackerClientsConfiguration {

@Value("${tracker.raygun.client.key}")
private String raygunClientKey;

@Bean
public RaygunClient raygunClient(){
    RaygunClient raygunClient = new RaygunClient(raygunClientKey);
    return raygunClient;
}

}
