package com.kairos.config.mongoEnv_config;



public interface EnvironmentConfig {


    String getDataBaseName();

    String getMongoUri();

    String getMongoHost();

    int getMongoPort();

}
