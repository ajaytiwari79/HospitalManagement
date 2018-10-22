package com.kairos.config.asw;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AWSS3BucketConfg {


    @Value("${cloud.aws.s3.region}")
    private String s3BucketRegion;

    @Value("${cloud.aws.s3.access.key.id}")
    private String awsAccessKey;

    @Value("${cloud.aws.s3.secret.access.key}")
    private String awsSecretAccessKey;

    @Bean
    public BasicAWSCredentials awsBasicCredentials() {
        return new BasicAWSCredentials(awsAccessKey, awsSecretAccessKey);

    }


    @Bean
    public AmazonS3 s3client() {


        return AmazonS3ClientBuilder
                .standard()
                .withForceGlobalBucketAccessEnabled(true)
                .withRegion(s3BucketRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsBasicCredentials()))
                .build();
    }

}
