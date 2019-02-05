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
    private String dbUri;

   // @Value("${spring.data.mongodb.port}")
    private int dbPort;

   // @Value("${spring.data.mongodb.host}")
    private String dbHost;

   // @Value("${spring.data.mongodb.username}")
    private String dbUserName;

    //@Value("${spring.data.mongodb.password}")
    private String dbPassword;

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

    public String getDbUri() {
        return dbUri;
    }

    public void setDbUri(String dbUri) {
        this.dbUri = dbUri;
    }

    public int getDbPort() {
        return dbPort;
    }

    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getS3BucketRegion() { return s3BucketRegion; }

    public void setS3BucketRegion(String s3BucketRegion) { this.s3BucketRegion = s3BucketRegion; }

    public String getAwsAccessKey() { return awsAccessKey; }

    public void setAwsAccessKey(String awsAccessKey) { this.awsAccessKey = awsAccessKey; }

    public String getAwsSecretAccessKey() { return awsSecretAccessKey; }

    public void setAwsSecretAccessKey(String awsSecretAccessKey) { this.awsSecretAccessKey = awsSecretAccessKey; }
}
