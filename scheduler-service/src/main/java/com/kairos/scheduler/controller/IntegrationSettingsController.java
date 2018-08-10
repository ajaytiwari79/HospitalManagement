package com.kairos.scheduler.controller;

import com.kairos.dto.IntegrationSettingsDTO;
import com.kairos.scheduler.persistence.model.scheduler_panel.IntegrationSettings;
import com.kairos.scheduler.service.scheduler_panel.IntegrationConfigurationService;
import com.kairos.scheduler.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.math.BigInteger;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import static com.kairos.scheduler.constants.ApiConstants.API_V1;

@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class IntegrationSettingsController {

    @Inject
    private IntegrationConfigurationService integrationConfigurationService;

    @ApiOperation(value = "Get integration services")
    @GetMapping("/unit/{unitId}/integration_service")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getIntegrationServices() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationConfigurationService.getAllIntegrationServices());
    }

    @ApiOperation(value = "Add integration service")
    @PostMapping("/integration_service")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addIntegrationService(@Validated @RequestBody IntegrationSettings objectToSave) {
        IntegrationSettingsDTO integrationSettings = integrationConfigurationService.addIntegrationConfiguration(objectToSave);
        if (integrationSettings == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, integrationSettings);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationSettings);
    }

    @ApiOperation(value = "Update integration service")
    @PutMapping("/integration_service/{integrationServiceId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateIntegrationService(@Validated @RequestBody IntegrationSettings integrationSettings, @PathVariable BigInteger integrationServiceId) {
        IntegrationSettingsDTO integrationSettingsDTO = integrationConfigurationService.updateIntegrationService(integrationServiceId, integrationSettings);
        if (integrationSettingsDTO == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, integrationSettingsDTO);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationSettingsDTO);
    }

    @ApiOperation(value = "Delete integration service")
    @DeleteMapping("/integration_service/{integrationServiceId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteIntegrationService(@PathVariable BigInteger integrationServiceId) {
        boolean isDeleted = integrationConfigurationService.deleteIntegrationService(integrationServiceId);
        if (isDeleted) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationConfigurationService.deleteIntegrationService(integrationServiceId));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);

    }



}
