package com.kairos.controller.activity;


import com.kairos.dto.activity.activity.ActivityPriorityDTO;
import com.kairos.service.activity.ActivityPriorityService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

@RestController
@RequestMapping(API_V1)
@Api(API_V1)
public class ActivityPrioritiesController {

    @Inject private ActivityPriorityService activityPriorityService;

    @ApiOperation("Create ActivityPriority At Country")
    @PostMapping(value = COUNTRY_URL+"/activity_priority")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createActivityPriorityAtCountry(@PathVariable Long countryId, @RequestBody @Valid ActivityPriorityDTO activityPriorityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityPriorityService.createActivityPriorityAtCountry(countryId,activityPriorityDTO));
    }

    @ApiOperation("Create ActivityPriority At Country")
    @PostMapping(value = UNIT_URL+"/activity_priority")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createActivityPriorityAtOrganization(@PathVariable Long unitId, @RequestBody @Valid ActivityPriorityDTO activityPriorityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityPriorityService.createActivityPriorityAtOrganization(unitId,activityPriorityDTO));
    }

    @ApiOperation("Update ActivityPriority At Country")
    @PutMapping(value = COUNTRY_URL+"/activity_priority")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateActivityPriorityAtCountry(@PathVariable Long countryId, @RequestBody @Valid ActivityPriorityDTO activityPriorityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityPriorityService.updateActivityPriorityAtCountry(countryId,activityPriorityDTO));
    }

    @ApiOperation("Get ActivityPriority At Country")
    @GetMapping(value = COUNTRY_URL+"/activity_priority")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivityPriorityAtCountry(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityPriorityService.getActivityPriorityAtCountry(countryId));
    }

    @ApiOperation("Update ActivityPriority At Country")
    @PutMapping(value = UNIT_URL+"/activity_priority")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateActivityPriorityAtOrganization(@PathVariable Long unitId, @RequestBody @Valid ActivityPriorityDTO activityPriorityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityPriorityService.updateActivityPriorityAtOrganization(unitId,activityPriorityDTO));
    }

    @ApiOperation("Get ActivityPriority At Country")
    @GetMapping(value = UNIT_URL+"/activity_priority")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivityPriorityAtOrganization(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityPriorityService.getActivityPriorityAtOrganization(unitId));
    }

    @ApiOperation("Delete ActivityPriority At Country")
    @DeleteMapping(value = COUNTRY_URL+"/activity_priority/{activityPriorityId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateActivityPriority(@PathVariable Long countryId, @PathVariable BigInteger activityPriorityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityPriorityService.deleteActivityPriorityFromCountry(activityPriorityId,countryId));
    }

    @ApiOperation("Delete ActivityPriority At Country")
    @DeleteMapping(value = UNIT_URL+"/activity_priority/{activityPriorityId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteActivityPriorityFromOrganization(@PathVariable Long unitId, @PathVariable BigInteger activityPriorityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityPriorityService.deleteActivityPriorityFromOrganization(activityPriorityId,unitId));
    }

    @ApiOperation("update ActivityPriority In Activity")
    @PutMapping(value = "/activity/{activityId}/activity_priority/{activityPriorityId}")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateActivityPriorityInActvitiy(@PathVariable BigInteger activityId, @PathVariable BigInteger activityPriorityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityPriorityService.updateActivityPriorityInActvitiy(activityPriorityId,activityId));
    }

}
