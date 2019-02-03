package com.kairos.config.mongo;

import com.kairos.config.mongo_converter.*;
import com.mongodb.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.MongoConstant.DB_NAME;
import static com.kairos.constants.MongoConstant.MONGO_URI;

/**
 * Created by prabjot on 4/10/16.
 */
@Configuration
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
@EnableMongoAuditing
@EnableTransactionManagement
public class MongoConfig extends AbstractMongoConfiguration implements EnvironmentAware {

    Environment environment;

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
        return new MongoCustomConversions(converterList);
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}