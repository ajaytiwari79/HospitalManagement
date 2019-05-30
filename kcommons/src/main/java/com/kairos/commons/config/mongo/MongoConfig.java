package com.kairos.commons.config.mongo;

/**
 * Created by pradeep on 09/04/19.
 */
//@Configuration
//@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
public class MongoConfig{/* extends AbstractMongoConfiguration{

    Environment environment;

    @Override
    @Bean("AuditLoggingMongoTemplate")
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(this.mongoDbFactory(), this.mappingMongoConverter());
        return mongoTemplate;
    }

    @Override
    protected String getDatabaseName() {
        return "auditLog";
    }
    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI("mongodb://localhost/auditLog"));
    }
    @Bean
    public MongoDatabase getDb(){
      return mongoClient().getDatabase(getDatabaseName());
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
*/

 /*   @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }*/
}