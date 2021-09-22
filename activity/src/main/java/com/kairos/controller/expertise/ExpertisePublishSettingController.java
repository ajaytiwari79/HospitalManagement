package com.kairos.controller.expertise;

import com.kairos.dto.activity.night_worker.ExpertiseNightWorkerSettingDTO;
import com.kairos.persistence.model.expertise.ExpertisePublishSetting;
import com.kairos.service.expertise.ExpertisePublishSettingService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;
import static com.kairos.constants.ApiConstants.UNIT_URL;

@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class ExpertisePublishSettingController {


    @Inject private
    ExpertisePublishSettingService expertisePublishSettingService;

    @ApiOperation(value = "get expertise publish setting")
    @GetMapping(value = COUNTRY_URL+"/expertise/{expertiseId}/expertise_publish_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertisePublishSettings(@PathVariable Long countryId, @PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertisePublishSettingService.getExpertisePublishSettings(countryId, expertiseId));
    }

    @ApiOperation(value = "update expertise publish setting")
    @PutMapping(value = COUNTRY_URL+"/expertise/{expertiseId}/expertise_publish_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateExpertisePublishSettings(@PathVariable Long countryId, @PathVariable Long expertiseId,
                                                                                  @RequestBody ExpertisePublishSetting expertisePublishSetting) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertisePublishSettingService.updateExpertisePublishSettings(countryId, expertiseId, expertisePublishSetting));
    }
    //---------------------UNIT LEVEL------------------//

    @ApiOperation(value = "get expertise publish setting for unit ")
    @GetMapping(value = UNIT_URL+"/expertise/{expertiseId}/expertise_publish_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertisePublishSettingsForUnit(@PathVariable Long expertiseId,@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertisePublishSettingService.getExpertisePublishSettingsForUnit(unitId,expertiseId));
    }
    @ApiOperation(value = "update expertise publish setting")
    @PutMapping(value = UNIT_URL+"/expertise/{expertiseId}/expertise_publish_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateExpertisePublishSettingsInUnit(@PathVariable Long unitId,@PathVariable Long expertiseId,
                                                                                        @RequestBody ExpertisePublishSetting expertisePublishSetting) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, expertisePublishSettingService.updateExpertisePublishSettingsInUnit(unitId, expertiseId,expertisePublishSetting));
    }

    @ApiOperation(value = "update EmploymentType And ExpertiseId")
    @GetMapping(value = UNIT_URL+"/update_employmentType_and_expertiseId")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateEmploymentTypeAndExpertiseId(@PathVariable Long unitId) {
        expertisePublishSettingService.updateEmploymentTypeAndExpertiseId(unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }
}
