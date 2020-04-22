package com.kairos;

import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.configuration.PermissionSchemaScanner;
import com.kairos.rest_client.UserIntegrationService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

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

        //flsVisitourChangeService.registerReceiver("visitourChange");
       // payRollSystemService.createDefaultPayRollSystemList();
        createPermissionModel();

    }

    private void createPermissionModel(){
        List<Map<String, Object>> permissionSchema= new PermissionSchemaScanner().createPermissionSchema(envConfigCommon.getModelPackagePath());
        userIntegrationService.createPermissionModels(permissionSchema);
    }
}
