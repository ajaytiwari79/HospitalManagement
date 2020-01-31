package com.kairos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.commons.service.mail.EmailServicesConfiguration;
import com.kairos.commons.service.mail.KMailService;
import com.kairos.configuration.PermissionSchemaScanner;
import com.kairos.service.kpermissions.PermissionService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.thymeleaf.TemplateEngine;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
@Configuration
public class ApplicationConfiguration {

    @Inject
    private EnvConfigCommon envConfigCommon;

    @Inject
    private PermissionService permissionService;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ApplicationContext applicationContext;

    @Bean
    PermissionSchemaProcessor prePermissionSchemaProcessor(){
        List<Map<String, Object>> permissionSchema= new PermissionSchemaScanner().createPermissionSchema(envConfigCommon.getModelPackagePath());
        return new PermissionSchemaProcessor(permissionSchema, permissionService, objectMapper);
    }

    @Bean
    KMailService kMailService(){
        EmailServicesConfiguration emailServicesConfiguration = applicationContext.getBean(EmailServicesConfiguration.class);
        EnvConfigCommon envConfigCommon = applicationContext.getBean(EnvConfigCommon.class);
        TemplateEngine templateEngine = applicationContext.getBean(TemplateEngine.class);
        return new KMailService(emailServicesConfiguration.getCurrentEmailService(),envConfigCommon,templateEngine);
    }

}
