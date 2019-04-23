package com.kairos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.configuration.PermissionSchemaScanner;
import com.kairos.service.kpermissions.PermissionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Configuration
public class ApplicationConfiguration {

    @Inject
    private EnvConfigCommon envConfigCommon;

    @Inject
    private PermissionService permissionService;

    @Inject
    private ObjectMapper objectMapper;

    @Bean
    PermissionSchemaProcessor prePermissionSchemaProcessor(){
        List<Map<String, Object>> permissionSchema= new PermissionSchemaScanner().createPermissionSchema(envConfigCommon.getModelPackagePath());
        return new PermissionSchemaProcessor(permissionSchema, permissionService, objectMapper);
    }
}
