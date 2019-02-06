package com.kairos.controller.night_worker;


import com.kairos.service.night_worker.ExpertiseNightWorkerSettingService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.dto.activity.night_worker.ExpertiseNightWorkerSettingDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class ExpertiseNightWorkerSettingController {

    @Inject
    ExpertiseNightWorkerSettingService expertiseNightWorkerSettingService;

    @ApiOperation(value = "create expertise night worker settings")
    @PostMapping(value = COUNTRY_URL+"/expertise/{expertiseId}/night_worker_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createExpertiseNightWorkerSettings(@PathVariable Long countryId, @PathVariable Long expertiseId,
                                                                               @RequestBody @Valid ExpertiseNightWorkerSettingDTO expertiseNightWorkerSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseNightWorkerSettingService.createExpertiseNightWorkerSettings(countryId, expertiseId, expertiseNightWorkerSettingDTO));
    }

    @ApiOperation(value = "get expertise night worker settings")
    @GetMapping(value = COUNTRY_URL+"/expertise/{expertiseId}/night_worker_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertiseNightWorkerSettings(@PathVariable Long countryId, @PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseNightWorkerSettingService.getExpertiseNightWorkerSettings(countryId, expertiseId));
    }

    @ApiOperation(value = "update expertise night worker settings")
    @PutMapping(value = COUNTRY_URL+"/expertise/{expertiseId}/night_worker_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateExpertiseNightWorkerSettings(@PathVariable Long countryId, @PathVariable Long expertiseId,
                                                                               @RequestBody @Valid ExpertiseNightWorkerSettingDTO expertiseNightWorkerSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseNightWorkerSettingService.updateExpertiseNightWorkerSettings(countryId, expertiseId, expertiseNightWorkerSettingDTO));
    }
    //---------------------UNIT LEVEL------------------//

    @ApiOperation(value = "get expertise night worker settings for unit ")
    @GetMapping(value = UNIT_URL+"/expertise/{expertiseId}/night_worker_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertiseNightWorkerSettingsForUnit(@PathVariable Long expertiseId,@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseNightWorkerSettingService.getExpertiseNightWorkerSettingsForUnit(unitId,expertiseId));
    }
    @ApiOperation(value = "update expertise night worker settings")
    @PutMapping(value = UNIT_URL+"/expertise/{expertiseId}/night_worker_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateExpertiseNightWorkerSettingsInUnit(@PathVariable Long unitId,@PathVariable Long expertiseId,
                                                                                  @RequestBody @Valid ExpertiseNightWorkerSettingDTO expertiseNightWorkerSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertiseNightWorkerSettingService.updateExpertiseNightWorkerSettingsInUnit(unitId, expertiseId,expertiseNightWorkerSettingDTO));
    }

}
