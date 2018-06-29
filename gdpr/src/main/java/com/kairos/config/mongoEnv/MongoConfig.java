package com.kairos.config.mongoEnv;

import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {


    @Autowired
    private EnvConfig environment;


    @Override
    public MongoClient mongoClient() {
        final List<MongoCredential> credentialList = Arrays.asList(MongoCredential.createCredential(environment.getMongoUserName(),environment.getDataBaseName(),environment.getMongoPassword().toCharArray()));
        return  new MongoClient( new ServerAddress(environment.getMongoHost(),environment.getMongoPort()) ,credentialList);
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