package com.kairos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.commons.service.mail.EmailServicesConfiguration;
import com.kairos.commons.service.mail.KMailService;
import com.kairos.configuration.PermissionSchemaScanner;
import com.kairos.rest_client.UserRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Configuration
public class ApplicationConfiguration {

    @Bean
    KMailService kMailService(EmailServicesConfiguration emailServicesConfiguration, EnvConfigCommon envConfigCommon, TemplateEngine templateEngine){
        return new KMailService(emailServicesConfiguration.getCurrentEmailService(),envConfigCommon,templateEngine);
    }
}
