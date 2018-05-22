package com.kairos.config.mongoEnv_config;

import org.springframework.beans.factory.annotation.Value;

public class DevlopmentEnvironment implements EnvironmentConfig{


    @Value("${spring.data.mongodb.database.dev}")
    private String dataBaseName;

    @Value("${spring.data.mongodb.uri.dev}")
    private String mongoUri;

    @Value("${spring.data.mongodb.port.dev}")
    private int mongoPort;

    @Value("${spring.data.mongodb.host.dev}")
    private String mongoHost;

    @Override
    public String getDataBaseName() {
        return dataBaseName;
    }

    @Override
    public String getMongoUri() {
        return mongoUri;
    }

    @Override
    public String getMongoHost() {
        return mongoHost;
    }

    @Override
    public int getMongoPort() {
        return mongoPort;
    }
}
