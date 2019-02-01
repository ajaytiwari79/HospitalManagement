package com.kairos.config.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource({"classpath:application-${spring.profiles.active}.properties"})
public class EnvConfig {


    //@Value("${spring.data.mongodb.database}")
    private String dataBaseName;

   // @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

   // @Value("${spring.data.mongodb.port}")
    private int mongoPort;

   // @Value("${spring.data.mongodb.host}")
    private String mongoHost;

   // @Value("${spring.data.mongodb.username}")
    private String mongoUserName;

    //@Value("${spring.data.mongodb.password}")
    private String mongoPassword;

    @Value("${cloud.aws.s3.region}")
    private String s3BucketRegion;

    @Value("${cloud.aws.s3.access.key.id}")
    private String awsAccessKey;

    @Value("${cloud.aws.s3.secret.access.key}")
    private String awsSecretAccessKey;


    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public String getMongoUri() {
        return mongoUri;
    }

    public void setMongoUri(String mongoUri) {
        this.mongoUri = mongoUri;
    }

    public int getMongoPort() {
        return mongoPort;
    }

    public void setMongoPort(int mongoPort) {
        this.mongoPort = mongoPort;
    }

    public String getMongoHost() {
        return mongoHost;
    }

    public void setMongoHost(String mongoHost) {
        this.mongoHost = mongoHost;
    }

    public String getMongoUserName() {
        return mongoUserName;
    }

    public void setMongoUserName(String mongoUserName) {
        this.mongoUserName = mongoUserName;
    }

    public String getMongoPassword() {
        return mongoPassword;
    }

    public void setMongoPassword(String mongoPassword) {
        this.mongoPassword = mongoPassword;
    }

    public String getS3BucketRegion() { return s3BucketRegion; }

    public void setS3BucketRegion(String s3BucketRegion) { this.s3BucketRegion = s3BucketRegion; }

    public String getAwsAccessKey() { return awsAccessKey; }

    public void setAwsAccessKey(String awsAccessKey) { this.awsAccessKey = awsAccessKey; }

    public String getAwsSecretAccessKey() { return awsSecretAccessKey; }

    public void setAwsSecretAccessKey(String awsSecretAccessKey) { this.awsSecretAccessKey = awsSecretAccessKey; }
}
