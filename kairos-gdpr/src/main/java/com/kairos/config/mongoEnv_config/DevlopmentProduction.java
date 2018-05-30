package com.kairos.config.mongoEnv_config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevlopmentProduction {

    @Bean
    public EnvironmentConfig getDevelopmentEnv() {
        return new DevlopmentEnvironment();
    }
}
