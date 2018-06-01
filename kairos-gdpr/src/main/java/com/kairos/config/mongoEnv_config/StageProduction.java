package com.kairos.config.mongoEnv_config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("stage")
public class StageProduction {

    @Bean
    public EnvironmentConfig getStageEnvironment()
    {
        return new StageEnvironment();
    }
}
