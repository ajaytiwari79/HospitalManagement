package com.kairos.config.mongoEnv_config;

import org.springframework.beans.factory.annotation.Value;

public class StageEnvironment implements EnvironmentConfig {


    @Value("${spring.data.mongodb.database.stage}")
    private String dataBaseName;

    @Value("${spring.data.mongodb.uri.stage}")
    private String mongoUri;

    @Value("${spring.data.mongodb.port.stage}")
    private int mongoPort;

    @Value("${spring.data.mongodb.host.stage}")
    private  String mongoHost;

    public StageEnvironment() {
    }


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
