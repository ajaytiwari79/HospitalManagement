package com.planner.appConfig.dbConfig;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import javax.inject.Inject;

/**
 * This is required to get another mongoTemplate instance with different database
 * of same mongod(server).
 */
@Configuration
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
public class MongoDb2ndInstanceConfig extends AbstractMongoConfiguration {

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI("mongodb://localhost/kairos"));
    }

    @Override
    protected String getDatabaseName() {
        return "kairos";
    }
    @Override
    @Bean("Activity")
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
        return mongoTemplate;
    }
    @Override
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoDbFactory(mongoClient(), this.getDatabaseName());
    }
    @Override
    public MappingMongoConverter mappingMongoConverter() throws Exception {

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext());
        converter.setCustomConversions(customConversions());
        return converter;
    }


}
