package com.kairos.controller.activity;


import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;

@RestController
@RequestMapping(API_V1 +COUNTRY_URL)
public class TimeTypeController {


    @Inject private TimeTypeService timeTypeService;

    @ApiOperation("Create a TimeType")
    @PostMapping(value = "/timeType/")
    public ResponseEntity<Map<String, Object>> createTimeType(@RequestBody List<TimeTypeDTO> timeTypeDTOS,@PathVariable Long countryId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.createTimeType(timeTypeDTOS,countryId));
    }

    @ApiOperation("Update a TimeType")
    @PutMapping(value = "/timeType/")
    public ResponseEntity<Map<String, Object>> updateTimeType(@RequestBody TimeTypeDTO timeTypeDTO,@PathVariable Long countryId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.updateTimeType(timeTypeDTO, countryId));
    }

    @ApiOperation("Get All TimeTypes")
    @GetMapping(value = "/timeType/")
    public ResponseEntity<Map<String, Object>> getAllTimeType(@PathVariable Long countryId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.getAllTimeType(null,countryId));
    }

    @ApiOperation("Delete a TimeType")
    @DeleteMapping(value = "/timeType/{timeTypeId}")
    public ResponseEntity<Map<String, Object>> deleteTimeType(@PathVariable BigInteger timeTypeId,@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.deleteTimeType(timeTypeId,countryId));
    }

    @ApiOperation("Create Default TimeTypes for a Country")
    @PostMapping(value = "/timeType/default")
    public ResponseEntity<Map<String, Object>> createDefaultTimeTypes(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.createDefaultTimeTypes(countryId));
    }

    @ApiOperation("Verify that time type is exists or not")
    @GetMapping(value = "/timeType/{timeTypeId}/verify")
    public ResponseEntity<Map<String, Object>> verifyTimeType(@PathVariable BigInteger timeTypeId,@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.existsByIdAndCountryId(timeTypeId,countryId));
    }

    @ApiOperation("update Skill tab of timetype")
    @PutMapping(value = "/time_type/{timeTypeId}/skill")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateSkillTabOfActivity(@RequestBody SkillActivityDTO skillActivityDTO, @PathVariable BigInteger timeTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.updateSkillTabOfTimeType(skillActivityDTO,timeTypeId));
    }

    @ApiOperation("get Bonus Tab of timetype")
    @GetMapping(value = "/time_type/{timeTypeId}/skill")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getSkillTabOfActivity(@PathVariable BigInteger timeTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.getSkillTabOfTimeType(timeTypeId));
    }


    //organization Mapping

    @ApiOperation("update organization Mapping details  of timeType Type")
    @PutMapping(value = "/time_type/{timeTypeId}/organizationMapping")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateOrgMappingDetailOfActivity(@RequestBody OrganizationMappingDTO organizationMappingDTO, @PathVariable BigInteger timeTypeId) {
        timeTypeService.updateOrgMappingDetailOfActivity(organizationMappingDTO, timeTypeId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @ApiOperation("get organization Mapping details  of timeType")
    @GetMapping(value = "/time_type/{timeTypeId}/organizationMapping")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getOrgMappingDetailOfActivity(@PathVariable BigInteger timeTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.getOrgMappingDetailOfTimeType(timeTypeId));
    }

    @ApiOperation("get Rules Tab of timeType")
    @GetMapping(value = "/time_type/{timeTypeId}/rules")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getRulesTab(@PathVariable Long countryId, @PathVariable BigInteger timeTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.getRulesTabOfTimeType(timeTypeId,countryId));
    }

    @ApiOperation("Update Rules Tab of timeType")
    @PutMapping(value = "/time_type/{timeTypeId}/rules")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateRulesTab(@RequestBody RulesActivityTabDTO rulesDTO, @PathVariable BigInteger timeTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.updateRulesTab(rulesDTO,timeTypeId));
    }

    //Phase Settings

    @ApiOperation("get Phase setting Tab of timeType")
    @GetMapping(value = "/time_type/{timeTypeId}/phase_settings")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getPhaseSettingTab(@PathVariable Long countryId, @PathVariable BigInteger timeTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.getPhaseSettingTabOfTimeType(timeTypeId,countryId));
    }

    @ApiOperation("Update Phase setting Tab of timeType")
    @PutMapping(value = "/time_type/{timeTypeId}/phase_settings")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updatePhaseSettingTab(@RequestBody PhaseSettingsActivityTab phaseSettingsActivityTab, @PathVariable BigInteger timeTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.updatePhaseSettingTab(phaseSettingsActivityTab,timeTypeId));
    }


    @ApiOperation("Update Time calculation Tab of timeType")
    @PutMapping(value = "/time_type/{timeTypeId}/timeCalculation")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateTimeCalculationTabOfActivity(@RequestBody TimeCalculationActivityDTO timeCalculationActivityDTO, @PathVariable BigInteger timeTypeId , @RequestParam boolean availableAllowActivity) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.updateTimeCalculationTabOfTimeType(timeCalculationActivityDTO,timeTypeId));
    }

    @ApiOperation("get getTime Calculation Tab of timeType")
    @GetMapping(value = "/time_type/{timeTypeId}/timeCalculation")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getTimeCalculationTabOfActivity(@PathVariable Long countryId, @PathVariable BigInteger timeTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeTypeService.getTimeCalculationTabOfTimeType(timeTypeId,countryId));
    }


}
