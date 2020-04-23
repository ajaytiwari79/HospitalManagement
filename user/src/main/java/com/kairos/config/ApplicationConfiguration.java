package com.kairos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.commons.service.mail.EmailServicesConfiguration;
import com.kairos.commons.service.mail.KMailService;
import com.kairos.configuration.PermissionSchemaScanner;
import com.kairos.service.kpermissions.PermissionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.thymeleaf.TemplateEngine;

import java.util.List;
import java.util.Map;

@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
@Configuration
public class ApplicationConfiguration {

    @Bean
    PermissionSchemaProcessor prePermissionSchemaProcessor(EnvConfigCommon envConfigCommon,PermissionService permissionService,ObjectMapper objectMapper){
        List<Map<String, Object>> permissionSchema= new PermissionSchemaScanner().createPermissionSchema(envConfigCommon.getModelPackagePath());
        return new PermissionSchemaProcessor(permissionSchema, permissionService, objectMapper);
    }

    @Bean
    KMailService kMailService(EmailServicesConfiguration emailServicesConfiguration,EnvConfigCommon envConfigCommon,TemplateEngine templateEngine){
       return new KMailService(emailServicesConfiguration.getCurrentEmailService(),envConfigCommon,templateEngine);
    }
}
