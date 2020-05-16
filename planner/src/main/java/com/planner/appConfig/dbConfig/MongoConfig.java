package com.planner.appConfig.dbConfig;

import com.planner.mongo_converter.*;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;



@Configuration
@PropertySource({"classpath:application-${spring.profiles.active}.properties" })
public class MongoConfig extends AbstractMongoConfiguration {
    @Value("${spring.data.mongodb.database}")
    private String DB_NAME;
    @Value("${spring.data.mongodb.uri}")
    private String MONGO_URI;


    @Override
    protected String getDatabaseName() {
        return DB_NAME;
    }
    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(MONGO_URI));
    }
    @Bean
    public DB getDb(){
      return  mongoClient().getDB(getDatabaseName());
    }
    @Bean
    @Override
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
        converterList.add(new LocalDateReadConverter());
        converterList.add(new LocalDateWriteConverter());
        converterList.add(new LocalTimeReadConverter());
        converterList.add(new LocalTimeWriteConverter());
        converterList.add(new LocalDateTimeWriteConverter());
        converterList.add(new LocalDateTimeReadConverter());
        return new MongoCustomConversions(converterList);
    }
/*
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }*/
}