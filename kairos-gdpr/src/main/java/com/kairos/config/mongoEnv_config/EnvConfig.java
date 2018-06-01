package com.kairos.config.mongoEnv_config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;



@Configuration
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
public class EnvConfig {


    @Value("${spring.data.mongodb.database}")
    private String dataBaseName;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.port}")
    private int mongoPort;

    @Value("${spring.data.mongodb.host}")
    private String mongoHost;


    public String getDataBaseName() {
        return dataBaseName;
    }

    public String getMongoUri() {
        return mongoUri;
    }

    public String getMongoHost() {
        return mongoHost;
    }

    public int getMongoPort() {
        return mongoPort;
    }
}
