package com.kairos.configuration;

import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.rest_client.UserRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class ApplicationConfiguration {

    @Inject
    private UserRestClient userRestClient;

    @Inject
    private EnvConfigCommon envConfigCommon;


    @Bean
    PermissionSchemaProcessor permissionInitProcessor(){
        PermissionSchemaProcessor permissionSchemaProcessor = new PermissionSchemaProcessor(envConfigCommon.getModelPackagePath(), userRestClient,envConfigCommon.getUserServiceUrl(),envConfigCommon);
        return permissionSchemaProcessor;
    }
}
