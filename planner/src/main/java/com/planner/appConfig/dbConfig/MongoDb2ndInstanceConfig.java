package com.planner.appConfig.dbConfig;

import com.kairos.activity.config.mongo_converter.*;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

import static com.planner.constants.ActivityMongoConstant.DB_NAME;
import static com.planner.constants.ActivityMongoConstant.MONGO_URI;

/**
 * This is required to get another mongoTemplate instance with different database
 * of same mongod(server).
 */
//@Configuration
//@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
public class MongoDb2ndInstanceConfig {//extends AbstractMongoConfiguration implements EnvironmentAware {

   /* Environment environment;



    @Override
    @Bean("ActivityMongoTemplate")
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(this.mongoDbFactory(), this.mappingMongoConverter());
        return mongoTemplate;
    }

    @Override
    protected String getDatabaseName() {
        return this.environment.getProperty(DB_NAME);
    }
    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(this.environment.getProperty(MONGO_URI)));
    }
    @Bean("kairosMongoDbFactory")
    public DB getDb(){
        return  mongoClient().getDB(getDatabaseName());
    }
    @Bean
    @Override
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
        converterList.add(new LocalDateReadConverter());
        converterList.add(new LocalDateWriteConverter());
        converterList.add(new LocalDateToStringReadConverter());
        converterList.add(new LocalTimeReadConverter());
        converterList.add(new LocalTimeWriteConverter());
        converterList.add(new LocalDateTimeWriteConverter());
        converterList.add(new LocalDateTimeReadConverter());
        return new MongoCustomConversions(converterList);
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }*/


}
