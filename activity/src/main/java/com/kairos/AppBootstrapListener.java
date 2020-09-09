package com.kairos;

import com.kairos.annotations.KPermissionActions;
import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.configuration.PermissionSchemaScanner;
import com.kairos.dto.kpermissions.ActionDTO;
import com.kairos.enums.kpermissions.PermissionAction;
import com.kairos.rest_client.UserIntegrationService;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

/**
 * Creates below mentioned bootstrap data(if Not Available)
 * 1. User
 * 2. Role
 * 3. Organization
 * 4. Units
 */
@Component
public class AppBootstrapListener implements ApplicationListener<ApplicationReadyEvent> {


    @Inject
    private EnvConfigCommon envConfigCommon;

    @Inject private UserIntegrationService userIntegrationService;


    /**
     * Executes on application ready event
     * Check's if data exists & calls createUsersAndRolesData
     */

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createPermissionModel();
        createActionPermissions();

    }

    private void createPermissionModel(){
            List<Map<String, Object>> permissionSchema= new PermissionSchemaScanner().createPermissionSchema(envConfigCommon.getModelPackagePath());
            userIntegrationService.createPermissionModels(permissionSchema);
     }

    public void createActionPermissions() {
            List<ActionDTO> permissionActions=new PermissionSchemaScanner().createActionPermissions(envConfigCommon.getControllerPackagePath());
            userIntegrationService.createActions(permissionActions);
    }
}
