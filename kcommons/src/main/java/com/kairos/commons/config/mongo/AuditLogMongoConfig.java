package com.kairos.commons.config.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pradeep on 09/04/19.
 */
//@Configuration
//@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
public class AuditLogMongoConfig extends AbstractMongoConfiguration implements EnvironmentAware{

    Environment environment;

    @Override
    @Bean("AuditLoggingMongoTemplate")
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(this.mongoDbFactory(), this.mappingMongoConverter());
        return mongoTemplate;
    }

    @Bean
    @Override
    public MappingMongoConverter mappingMongoConverter() throws Exception {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext());
        converter.setCustomConversions(customConversions());
        converter.afterPropertiesSet();
        return converter;
    }

    @Override
    @Bean("AuditLoggingMongoDbFactory")
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoDbFactory(mongoClient(), getDatabaseName());
    }

    @Override
    protected String getDatabaseName() {
        return environment.getProperty("auditLogging");
    }
    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(environment.getProperty("auditLogging.uri")));
    }
    @Bean("AuditLoggingMongoDataBase")
    public MongoDatabase getDb(){
        return mongoClient().getDatabase(getDatabaseName());
    }

    @Override
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();
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