package com.kairos.config.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource({"classpath:application-${spring.profiles.active}.properties"})
public class EnvConfig {

    @Value("${cloud.aws.s3.region}")
    private String s3BucketRegion;

    @Value("${cloud.aws.s3.access.key.id}")
    private String awsAccessKey;

    @Value("${cloud.aws.s3.secret.access.key}")
    private String awsSecretAccessKey;


    public String getS3BucketRegion() { return s3BucketRegion; }

    public void setS3BucketRegion(String s3BucketRegion) { this.s3BucketRegion = s3BucketRegion; }

    public String getAwsAccessKey() { return awsAccessKey; }

    public void setAwsAccessKey(String awsAccessKey) { this.awsAccessKey = awsAccessKey; }

    public String getAwsSecretAccessKey() { return awsSecretAccessKey; }

    public void setAwsSecretAccessKey(String awsSecretAccessKey) { this.awsSecretAccessKey = awsSecretAccessKey; }

}
