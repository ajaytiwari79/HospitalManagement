package com.kairos.config.mongo;

import com.kairos.config.mongo_converter.*;
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
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by prabjot on 4/10/16.
 */
@Configuration
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
public class MongoConfig extends AbstractMongoConfiguration implements EnvironmentAware {

    Environment environment;

    public static final String DB_NAME = "spring.data.mongodb.database";

    public static final String DB_URL = "spring.data.mongodb.host";

    public static final String DB_PORT = "spring.data.mongodb.port";

    public static final String MONGO_URI = "spring.data.mongodb.uri";

    @Override
    protected String getDatabaseName() {
        return this.environment.getProperty(DB_NAME);
    }
    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(this.environment.getProperty(MONGO_URI)));
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
        converterList.add(new LocalDateToStringReadConverter());
        converterList.add(new LocalTimeReadConverter());
        converterList.add(new LocalTimeWriteConverter());
        converterList.add(new LocalDateTimeWriteConverter());
        converterList.add(new LocalDateTimeReadConverter());
        converterList.add(new ZonedDateTimeReadConverter());
        converterList.add(new ZonedDateTimeWriteConverter());
        return new MongoCustomConversions(converterList);
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}