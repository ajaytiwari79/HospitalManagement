package com.kairos.commons.service.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.inject.Inject;

//TODO create profile based email configuration properties file

@Configuration
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
public class EmailServicesConfiguration {

    @Value("${aws.mail.server.region}")
    private String mailRegion;

    @Value("${email.provider.active}")
    private String emailProvider;

    @Inject
    private ApplicationContext applicationContext;


    @Bean
    public AmazonSesEmailService amazonSesEmailService(){
        return new AmazonSesEmailService(mailRegion);
    }


    public EmailService getCurrentEmailService(){
        EmailService emailService;
        if(emailProvider.equals("AWS-SES")){
            emailService = applicationContext.getBean(AmazonSesEmailService.class);
        }else if(emailProvider.equals("SENDGRID")){
            emailService = applicationContext.getBean(SendGridMailService.class);
        }else{
            return null;
        }
        return emailService;
    }
}
