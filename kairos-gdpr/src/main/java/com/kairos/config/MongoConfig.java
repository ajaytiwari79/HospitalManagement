package com.kairos.config;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import static com.kairos.constant.Mongoconstant.DB_NAME;
import static com.kairos.constant.Mongoconstant.MONGO_URI;

@Configuration
@PropertySource("classpath:application.properties")
public class MongoConfig extends AbstractMongoConfiguration implements EnvironmentAware {


    Environment environment;


    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(this.environment.getProperty(MONGO_URI)));
    }

    @Override
    protected String getDatabaseName() {
        return this.environment.getProperty(DB_NAME);
    }

    @Bean
    public DB getDb() {
        return mongoClient().getDB(getDatabaseName());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
