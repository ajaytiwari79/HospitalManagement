package com.kairos;

import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.configuration.PermissionSchemaScanner;
import com.kairos.dto.kpermissions.ActionDTO;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.redis.RedisService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.newHashSet;

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

    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject private RedisService redisService;


    /**
     * Executes on application ready event
     * Check's if data exists & calls createUsersAndRolesData
     */

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        redisService.removeKeyFromCacheAsyscronously(newHashSet("find*","get*"));
        createPermissionModel();
        createActionPermissions();

    }

    private void createPermissionModel() {
        try {
            List<Map<String, Object>> permissionSchema = new PermissionSchemaScanner().createPermissionSchema(envConfigCommon.getModelPackagePath());
            userIntegrationService.createPermissionModels(permissionSchema);
        } catch (Exception ignored) {
        }

    }

    public void createActionPermissions() {
        try {
            List<ActionDTO> permissionActions = new PermissionSchemaScanner().createActionPermissions(envConfigCommon.getControllerPackagePath());
            userIntegrationService.createActions(permissionActions);
        } catch (Exception ignored) {
        }
    }
}
