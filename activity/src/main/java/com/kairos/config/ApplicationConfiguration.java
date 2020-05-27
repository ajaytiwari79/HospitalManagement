package com.kairos.config;

import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.commons.service.mail.EmailServicesConfiguration;
import com.kairos.commons.service.mail.KMailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;

@Configuration
public class ApplicationConfiguration {

    @Bean
    KMailService kMailService(EmailServicesConfiguration emailServicesConfiguration, EnvConfigCommon envConfigCommon, TemplateEngine templateEngine){
        return new KMailService(emailServicesConfiguration.getCurrentEmailService(),envConfigCommon,templateEngine);
    }
}
