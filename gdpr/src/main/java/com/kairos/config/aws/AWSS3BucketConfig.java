package com.kairos.config.aws;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.kairos.config.env.EnvConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;


@Configuration
class AWSS3BucketConfig {


    @Inject
    private EnvConfig envConfig;


    @Bean
    public AmazonS3Client s3client() {


        return (AmazonS3Client) AmazonS3ClientBuilder
                .standard()
                .withForceGlobalBucketAccessEnabled(true)
                .withRegion(envConfig.getS3BucketRegion())
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(envConfig.getAwsAccessKey(), envConfig.getAwsSecretAccessKey())))
                .build();
    }

}
