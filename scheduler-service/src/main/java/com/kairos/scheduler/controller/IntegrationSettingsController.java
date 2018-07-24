package com.kairos.scheduler.controller;

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

import static com.kairos.scheduler.constants.ApiConstants.API_INTEGRATIONCONFIGURATION_URL;

@RestController
@RequestMapping(API_INTEGRATIONCONFIGURATION_URL)
@Api(API_INTEGRATIONCONFIGURATION_URL)
public class IntegrationConfigurationController {

    @Inject
    private IntegrationConfigurationService integrationConfigurationService;

    @ApiOperation(value = "Get integration services")
    @RequestMapping(value = "/unit/{unitId}/integration_service", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getIntegrationServices() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationConfigurationService.getAllIntegrationServices());
    }

    @ApiOperation(value = "Add integration service")
    @RequestMapping(value = "/integration_service", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addIntegrationService(@Validated @RequestBody IntegrationSettings objectToSave) {
        HashMap<String, Object> integrationConfiguration = integrationConfigurationService.addIntegrationConfiguration(objectToSave);
        if (integrationConfiguration == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, integrationConfiguration);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationConfiguration);
    }

    @ApiOperation(value = "Update integration service")
    @RequestMapping(value = "/integration_service/{integrationServiceId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateIntegrationService(@Validated @RequestBody IntegrationSettings integrationSettings, @PathVariable BigInteger integrationServiceId) {
        HashMap<String, Object> updatedObject = integrationConfigurationService.updateIntegrationService(integrationServiceId, integrationSettings);
        if (updatedObject == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, updatedObject);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, updatedObject);
    }

    @ApiOperation(value = "Delete integration service")
    @RequestMapping(value = "/integration_service/{integrationServiceId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteIntegrationService(@PathVariable BigInteger integrationServiceId) {
        boolean isDeleted = integrationConfigurationService.deleteIntegrationService(integrationServiceId);
        if (isDeleted) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationConfigurationService.deleteIntegrationService(integrationServiceId));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);

    }



}
