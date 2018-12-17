package com.kairos.config.mail;

import com.kairos.constants.AppConstant;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;

/**
 * Created by prabjot on 28/11/16.
 */

@Configuration
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
public class SpringMailConfig implements ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;
    private Environment environment;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }


    @Bean
    public JavaMailSender mailSender() throws IOException {

        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Basic mail sender configuration, based on emailconfig.properties
        mailSender.setHost(this.environment.getProperty(AppConstant.HOST));
        mailSender.setPort(Integer.parseInt(this.environment.getProperty(AppConstant.PORT)));
        mailSender.setProtocol(this.environment.getProperty(AppConstant.PROTOCOL));
        mailSender.setUsername(this.environment.getProperty(AppConstant.MAIL_USERNAME));
        mailSender.setPassword(this.environment.getProperty(AppConstant.MAIL_AUTH));

        // JavaMail-specific mail sender configuration, based on javamail.properties

        return mailSender;
    }

//    @Bean
//    public ClassLoaderTemplateResolver emailTemplateResolver() {
//        ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
//        emailTemplateResolver.setPrefix("templates/mail/");
//        emailTemplateResolver.setSuffix(".html");
//        emailTemplateResolver.setTemplateMode("HTML5");
//        emailTemplateResolver.setCharacterEncoding(AppConstant.EMAIL_TEMPLATE_ENCODING);
//        emailTemplateResolver.setOrder(1);
//        return emailTemplateResolver;
//    }



}
