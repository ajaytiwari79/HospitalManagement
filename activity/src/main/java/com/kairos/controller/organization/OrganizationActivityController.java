package com.kairos.controller.organization;

import com.kairos.activity.activity.ActivityDTO;
import com.kairos.activity.activity.activity_tabs.*;
import com.kairos.persistence.model.activity.tabs.OptaPlannerSettingActivityTab;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.organization.OrganizationActivityService;
import com.kairos.util.response.ResponseHandler;
import com.kairos.wrapper.activity.RulesActivityTabDTO;
import com.kairos.wrapper.activity.SkillActivityDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by vipul on 5/12/17.
 */
@RestController
@Api(API_ORGANIZATION_UNIT_URL)
@RequestMapping(API_ORGANIZATION_UNIT_URL)
public class OrganizationActivityController {
    @Inject
    private OrganizationActivityService organizationActivityService;
    @Inject
    private ActivityService activityService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;

    @ApiOperation("Create Activity")
    @GetMapping(value = "/activity/{activityId}/copy-settings")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> copyActivity(@PathVariable Long unitId, @PathVariable BigInteger activityId, @RequestParam(value = "checked") boolean checked) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.copyActivity(unitId, activityId, checked));
    }

    @ApiOperation("Get all activity based on unitId")
    @GetMapping(value = "/activity_mapping")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivityMappingDetails(@PathVariable Long unitId, @RequestParam("type") String type) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getActivityMappingDetails(unitId, type));
    }

    @ApiOperation("Get all activity based on unitId")
    @GetMapping(value = "/activity_with_selected")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivityByUnitId(@PathVariable Long unitId, @RequestParam("type") String type) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getAllActivityByUnitAndDeleted(unitId));
    }

    @ApiOperation("Update icon  in Activity")
    @PostMapping(value = "/activity/{activityId}/icon")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> addIconInActivity(@PathVariable BigInteger activityId, @RequestParam("file") MultipartFile file) throws
            IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.addIconInActivity(activityId, file));
    }

    @ApiOperation("Update GeneralActivityTab Tab of Activity")
    @PutMapping(value = "/activity/general")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateGeneralTab(@PathVariable Long unitId, @RequestBody GeneralActivityTabDTO generalDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.updateGeneralTab(generalDTO, unitId));
    }

    @ApiOperation("get General Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/general")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getGeneralTab(@PathVariable Long unitId, @PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getGeneralTabOfActivity(activityId, unitId));
    }


    @ApiOperation("get balanceSettings Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/balanceSettings")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getBalanceSettingsTab(@PathVariable BigInteger activityId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getBalanceSettingsTabOfType(activityId, unitId));
    }

    @ApiOperation("Update Balance Settings Tab of Activity")
    @PutMapping(value = "/activity/balanceSettings")
        //  @PreAuthorize("@S.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateBalanceSettingsTab(@RequestBody BalanceSettingActivityTabDTO balanceDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateBalanceTab(balanceDTO));
    }

    @ApiOperation("get Rules Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/rules")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getRulesTab(@PathVariable BigInteger activityId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getRulesTabOfActivity(activityId, unitId));
    }

    @ApiOperation("Update Rules Tab of Activity")
    @PutMapping(value = "/activity/rules")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateRulesTab(@RequestBody RulesActivityTabDTO rulesDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateRulesTab(rulesDTO));
    }


    @ApiOperation("get getTime Calculation Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/timeCalculation")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getTimeCalculationTabOfActivity(@PathVariable BigInteger activityId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getTimeCalculationTabOfActivity(activityId, unitId));
    }

    @ApiOperation("Update Time calculation Tab of Activity")
    @PutMapping(value = "/activity/timeCalculation")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateTimeCalculationTabOfActivity(@RequestBody TimeCalculationActivityDTO timeCalculationActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateTimeCalculationTabOfActivity(timeCalculationActivityDTO));
    }


    @ApiOperation("Update IndividualPoints Tab of Activity")
    @PutMapping(value = "/activity/individualPoints")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateIndividualPointsTab(@RequestBody IndividualPointsActivityTabDTO individualPointsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateIndividualPointsTab(individualPointsDTO));
    }

    @ApiOperation("get IndividualPoints Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/individualPoints")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getIndividualPointsTab(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getIndividualPointsTabOfActivity(activityId));
    }


    @ApiOperation("delete an activity")
    @DeleteMapping(value = "/activity/{activityId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteActivity(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.deleteActivity(activityId));
    }


    @ApiOperation("Update compositeShifts Tab of Activity")
    @PutMapping(value = "/activity/{activityId}/compositeShifts")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> assignCompositeActivitiesInActivity(@PathVariable BigInteger activityId,@RequestBody List<CompositeShiftActivityDTO> compositeShiftActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.assignCompositeActivitiesInActivity(activityId,compositeShiftActivityDTO));
    }

    @ApiOperation("get compositeShifts Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/compositeShifts")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getCompositeShiftTabOfActivity(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getCompositeShiftTabOfActivity(activityId));
    }


    @ApiOperation("Update notes Tab of Activity")
    @PutMapping(value = "/activity/notes")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateNotesTabOfActivity(@RequestBody NotesActivityDTO notesActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateNotesTabOfActivity(notesActivityDTO));
    }

    @ApiOperation("get notes Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/notes")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getNotesTabOfActivity(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getNotesTabOfActivity(activityId));
    }

    //Api for uploading Documents in Notes Tab
    @ApiOperation("Upload Notes  in Activity")
    @PostMapping(value = "/activity/{activityId}/upload_note")
    ResponseEntity<Map<String, Object>> addDocumentInNotesTab(@PathVariable BigInteger activityId, @RequestParam("file") MultipartFile file) throws
            IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.addDocumentInNotesTab(activityId, file));
    }

    @ApiOperation("Update Communication Tab of Activity")
    @PutMapping(value = "/activity/communication")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateCommunicationTabOfActivity(@RequestBody CommunicationActivityDTO communicationActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateCommunicationTabOfActivity(communicationActivityDTO));
    }

    @ApiOperation("get CommunicationTab of Activity")
    @GetMapping(value = "/activity/{activityId}/communication")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getCommunicationTabOfActivity(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getCommunicationTabOfActivity(activityId));
    }

    //updateBonusTabOfActivityType

    @ApiOperation("Update Bonus Tab of Activity")
    @PutMapping(value = "/activity/bonus")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateBonusTabOfActivity(@RequestBody BonusActivityDTO bonusActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateBonusTabOfActivity(bonusActivityDTO));
    }

    @ApiOperation("get Bonus Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/bonus")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getBonusTabOfActivity(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getBonusTabOfActivity(activityId));
    }

    //Permissions

    @ApiOperation("Update Permissions Tab of Activity")
    @PutMapping(value = "/activity/permission_settings")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updatePermissionsTabOfActivity(@RequestBody PermissionsActivityTabDTO permissionsActivityTabDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updatePermissionsTabOfActivity(permissionsActivityTabDTO));
    }

    @ApiOperation("get Permissions Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/permission_settings")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getPermissionsTabOfActivity(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getPermissionsTabOfActivity(activityId));
    }
    // skills

    @ApiOperation("update Skill tab of activity Type")
    @PutMapping(value = "/activity/skill")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateSkillTabOfActivity(@RequestBody SkillActivityDTO skillActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateSkillTabOfActivity(skillActivityDTO));
    }

    @ApiOperation("get Bonus Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/skill")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getSkillTabOfActivity(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getSkillTabOfActivity(activityId));
    }

    // optaPlannerSetting
    @ApiOperation("get Opta PlannerSetting tab data of activity type")
    @GetMapping(value = "/activity/{activityId}/opta_planner_settings")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getOptaPlannerSettingsTabOfActivity(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getOptaPlannerSettingsTabOfActivity(activityId));
    }

    @ApiOperation("update Opta PlannerSetting  details  of activity Type")
    @PutMapping(value = "/activity/{activityId}/opta_planner_settings")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateOrgMappingDetailOfActivity(@PathVariable BigInteger activityId, @RequestBody OptaPlannerSettingActivityTab optaPlannerSettingActivityTab) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateOptaPlannerSettingsTabOfActivity(activityId, optaPlannerSettingActivityTab));
    }

    // cta_wta_settings
    @ApiOperation("get cta_response and wta settings tab data of activity type")
    @GetMapping(value = "/activity/{activityId}/cta_wta_settings")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getCtaAndWtaSettingsTabOfActivity(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getCtaAndWtaSettingsTabOfActivity(activityId));
    }

    @ApiOperation("update cta_response and wta settings  of activity Type")
    @PutMapping(value = "/activity/cta_wta_settings")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateCtaAndWtaSettingsTabOfActivity(@RequestBody CTAAndWTASettingsActivityTabDTO ctaAndWtaSettingsActivityTabDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateCtaAndWtaSettingsTabOfActivity(ctaAndWtaSettingsActivityTabDTO));
    }

    @ApiOperation("update organization Mapping details  of activity Type")
    @PutMapping(value = "/activity/{activityId}/organizationMapping")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateOrgMappingDetailOfActivity(@RequestBody OrganizationMappingActivityDTO organizationMappingActivityDTO, @PathVariable BigInteger activityId) {
        activityService.updateOrgMappingDetailOfActivity(organizationMappingActivityDTO, activityId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }

    @ApiOperation("get organization Mapping details  of Activity")
    @GetMapping(value = "/activity/{activityId}/organizationMapping")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getOrgMappingDetailOfActivity(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getOrgMappingDetailOfActivity(activityId));
    }

    @ApiOperation("Get all activity based on unitId for CTA default data")
    @GetMapping(value = "/activity/cta_wta_setting")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnitActivityForDefaultData(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.findAllActivityWithCtaWtaSettingByUnit(unitId));
    }

    @ApiOperation("copy Activity")
    @PostMapping(value = "/activity/{activityId}/copy_activity")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> copyActivityDetails(@PathVariable Long unitId, @PathVariable BigInteger activityId, @RequestBody @Valid ActivityDTO activity) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.copyActivityDetails(unitId, activityId, activity));
    }

    @ApiOperation("get location settings data of activity")
    @GetMapping(value = "/activity/{activityId}/location_settings")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getLocationsTabOfActivity(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getLocationsTabOfActivity(activityId));
    }

    @ApiOperation("update location settings of activity ")
    @PutMapping(value = "/activity/location_settings")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateLocationsTabOfActivity(@RequestBody LocationActivityTabDTO locationActivityTabDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateLocationsTabOfActivity(locationActivityTabDTO));
    }

    @ApiOperation(value = "Init optplanner integration")
    @RequestMapping(value = "/planner_integration", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> initialOptaplannerSync(@PathVariable Long organizationId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.initialOptaplannerSync(organizationId, unitId));
    }

    @ApiOperation("Get all activity based on unitId")
    @GetMapping(value = "/orders_and_activities")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivitiesWithBalanceSettings(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getActivitiesWithBalanceSettings(unitId));
    }

    @ApiOperation("Get all activity based on country")
    @GetMapping(value = "/activities_with_time_types")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivitiesWithTimeTypes(@PathVariable Long unitId, @RequestParam Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getActivitiesWithTimeTypesByUnit(unitId, countryId));

    }

    @ApiOperation(value = "Create default data for  Organization")
    @RequestMapping(value = "/organization_default_data", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createDefaultDataForOrganization(@PathVariable long unitId, @RequestParam Long countryId, @RequestParam Long parentOrganizationId, @RequestParam List<Long> orgTypeIds, @RequestParam List<Long> orgSubTypeIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationActivityService.createDefaultDataForOrganization(unitId, parentOrganizationId, countryId,orgTypeIds, orgSubTypeIds));
    }

}
