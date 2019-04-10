package com.kairos.config;

import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.configuration.PermissionSchemaScanner;
import com.kairos.rest_client.UserRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Configuration
public class ApplicationConfiguration {

    @Inject
    private UserRestClient userRestClient;

    @Inject
    private EnvConfigCommon envConfigCommon;
    

    @Bean
    PermissionSchemaProcessor prePermissionSchemaProcessor(){
        List<Map<String, Object>> permissionSchema= new PermissionSchemaScanner().createPermissionSchema(envConfigCommon.getModelPackagePath());
        PermissionSchemaProcessor permissionSchemaProcessor = new PermissionSchemaProcessor(permissionSchema, userRestClient,envConfigCommon.getUserServiceUrl(), envConfigCommon.getKpermissionDataPublish());
        return permissionSchemaProcessor;
    }
}
