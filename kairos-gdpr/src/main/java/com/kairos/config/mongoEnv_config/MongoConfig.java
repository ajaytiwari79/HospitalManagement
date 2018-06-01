package com.kairos.config.mongoEnv_config;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {


    @Autowired
    private EnvironmentConfig environment;


    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(environment.getMongoUri()));
    }

    @Override
    protected String getDatabaseName() {
        return environment.getDataBaseName();
    }

    @Bean
    public DB getDb() {
        return mongoClient().getDB(getDatabaseName());
    }


}