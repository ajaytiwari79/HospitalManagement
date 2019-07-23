package com.planner.appConfig.dbConfig;

import com.kairos.activity.config.mongo_converter.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

import static com.planner.constants.ActivityMongoConstant.DB_NAME;
import static com.planner.constants.ActivityMongoConstant.MONGO_URI;

/**
 * This is required to get another mongoTemplate instance with different database
 * of same mongod(server).
 */
@Configuration
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
public class MongoDb2ndInstanceConfig extends AbstractMongoConfiguration implements EnvironmentAware {

    Environment environment;



    @Override
    @Bean("ActivityMongoTemplate")
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(this.mongoDbFactory(), this.mappingMongoConverter());
        return mongoTemplate;
    }

    @Override
    @Bean("kairosMongoDbFactory")
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoDbFactory(mongoClient(), getDatabaseName());
    }

    @Override
    protected String getDatabaseName() {
        return this.environment.getProperty(DB_NAME);
    }
    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(this.environment.getProperty(MONGO_URI)));
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
