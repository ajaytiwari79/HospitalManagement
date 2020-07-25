package com.kairos.controller.organization;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.CommunicationActivityDTO;
import com.kairos.dto.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.persistence.model.activity.tabs.ActivityOptaPlannerSetting;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.organization.OrganizationActivityService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.API_UNIT_URL;

/**
 * Created by vipul on 5/12/17.
 */
@RestController
@Api(API_UNIT_URL)
@RequestMapping(API_UNIT_URL)
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
    public ResponseEntity<Map<String, Object>> getActivityMappingDetails(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getActivityMappingDetails(unitId));
    }

    @ApiOperation("Get all activity based on unitId")
    @GetMapping(value = "/activity_with_selected")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivityByUnitId(@PathVariable Long unitId, @RequestParam(value = "includeTeamActivity",required = false) boolean includeTeamActivity) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getAllActivityByUnit(unitId,includeTeamActivity));
    }

    @ApiOperation("Update icon  in Activity")
    @PostMapping(value = "/activity/{activityId}/icon")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> addIconInActivity(@PathVariable BigInteger activityId, @RequestParam("file") MultipartFile file) throws
            IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.addIconInActivity(activityId, file));
    }

    @ApiOperation("Update ActivityGeneralSettings Tab of Activity")
    @PutMapping(value = "/activity/general")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateGeneralTab(@PathVariable Long unitId, @RequestBody ActivityGeneralSettingsDTO generalDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.updateGeneralTab(generalDTO, unitId));
    }

    @ApiOperation("get General Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/general")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getGeneralTab(@PathVariable Long unitId, @PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getGeneralTabOfActivity(activityId, unitId));
    }

    @ApiOperation("Update Translations of Activity in a unit ")
    @PutMapping(value = "/activity/{activityId}/unit_language_settings")
    ResponseEntity<Map<String, Object>> updateLanguageSettings(@NotEmpty  @PathVariable Long unitId, @NotEmpty @PathVariable BigInteger activityId, @NotNull @RequestBody Map<String, TranslationInfo> translationMap) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.updateUnitActivityTranslationDetails(activityId,unitId,translationMap));
    }


    /*@ApiOperation("get balanceSettings Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/balanceSettings")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getBalanceSettingsTab(@PathVariable BigInteger activityId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getBalanceSettingsTabOfType(activityId, unitId));
    }*/

   /* @ApiOperation("Update Balance Settings Tab of Activity")
    @PutMapping(value = "/activity/balanceSettings")
        //  @PreAuthorize("@S.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateBalanceSettingsTab(@RequestBody ActivityBalanceSettingDTO balanceDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateBalanceTab(balanceDTO));
    }*/

    @ApiOperation("get Rules Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/rules")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getRulesTab(@PathVariable BigInteger activityId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getRulesTabOfActivity(activityId, unitId));
    }

    @ApiOperation("Update Rules Tab of Activity")
    @PutMapping(value = "/activity/rules")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateRulesTab(@RequestBody ActivityRulesSettingsDTO rulesDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateRulesTab(rulesDTO,true));
    }

    // Phase Settings
    @ApiOperation("get Phase setting Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/phase_settings")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getPhaseSettingTab(@PathVariable BigInteger activityId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getPhaseSettingTabOfActivity(activityId, unitId));
    }

    @ApiOperation("Update Phase setting Tab of Activity")
    @PutMapping(value = "/activity/phase_settings")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updatePhaseSetticopyngTab(@RequestBody ActivityPhaseSettings activityPhaseSettings) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updatePhaseSettingTab(activityPhaseSettings));
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
    ResponseEntity<Map<String, Object>> updateTimeCalculationTabOfActivity(@RequestBody TimeCalculationActivityDTO timeCalculationActivityDTO,@RequestParam boolean availableAllowActivity) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateTimeCalculationTabOfActivity(timeCalculationActivityDTO,availableAllowActivity));
    }


    @ApiOperation("Update IndividualPoints Tab of Activity")
    @PutMapping(value = "/activity/individualPoints")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateIndividualPointsTab(@RequestBody ActivityIndividualPointsSettingsDTO individualPointsDTO) {
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


/*
    @ApiOperation("Update compositeShifts Tab of Activity")
    @PutMapping(value = "/activity/{activityId}/allowed_activities")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> assignCompositeActivitiesInActivity(@PathVariable BigInteger activityId, @RequestBody List<CompositeShiftActivityDTO> compositeShiftActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.assignCompositeActivitiesInActivity(activityId, compositeShiftActivityDTO));
    }
*/

    @ApiOperation("Update child Activivty of Activity")
    @PutMapping(value = "/activity/{activityId}/child_activities")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> assignChildActivitiesInActivity(@PathVariable BigInteger activityId, @RequestBody Set<BigInteger> childActivitiesIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.assignChildActivitiesInActivity(activityId,childActivitiesIds));
    }

    @ApiOperation("get compositeShifts Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/allowed_child_activities")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getCompositeShiftTabOfActivity(@PathVariable BigInteger activityId,@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getCompositeAndChildActivityOfUnitActivity(activityId,unitId));
    }


   /* @ApiOperation("Update notes Tab of Activity")
    @PutMapping(value = "/activity/notes")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateNotesTabOfActivity(@RequestBody NotesActivityDTO notesActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateNotesTabOfActivity(notesActivityDTO));
    }*/

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
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.addDocumentInNotesTab(activityId, file));
    }

    @ApiOperation("Update Communication Tab of Activity")
    @PutMapping(value = "/activity/communication")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateCommunicationTabOfActivity(@RequestBody CommunicationActivityDTO communicationActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.updateCommunicationTabOfActivity(communicationActivityDTO, true));
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
    ResponseEntity<Map<String, Object>> updateOrgMappingDetailOfActivity(@PathVariable BigInteger activityId, @RequestBody ActivityOptaPlannerSetting activityOptaPlannerSetting) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateOptaPlannerSettingsTabOfActivity(activityId, activityOptaPlannerSetting));
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
    ResponseEntity<Map<String, Object>> updateCtaAndWtaSettingsTabOfActivity(@RequestBody ActivityCTAAndWTASettingsDTO activityCTAAndWTASettingsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateCtaAndWtaSettingsTabOfActivity(activityCTAAndWTASettingsDTO));
    }

    @ApiOperation("update organization Mapping details  of activity Type")
    @PutMapping(value = "/activity/{activityId}/organizationMapping")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateOrgMappingDetailOfActivity(@RequestBody OrganizationMappingDTO organizationMappingDTO, @PathVariable BigInteger activityId) {
        activityService.updateOrgMappingDetailOfActivity(organizationMappingDTO, activityId);
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
    ResponseEntity<Map<String, Object>> updateLocationsTabOfActivity(@RequestBody ActivityLocationSettingsDTO activityLocationSettingsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateLocationsTabOfActivity(activityLocationSettingsDTO));
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
    @PostMapping(value = "/organization_default_data")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createDefaultDataForOrganization(@PathVariable long unitId, @RequestBody OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationActivityService.createDefaultDataForOrganization(unitId, orgTypeAndSubTypeDTO));
    }

    @ApiOperation("Remove uploaded Attachments in Activity")
    @DeleteMapping(value = "/activity/{activityId}/remove_uploaded_attachments")
    ResponseEntity<Map<String, Object>> removeAttachementsFromActivity(@PathVariable BigInteger activityId, @RequestParam boolean removeNotes) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.removeAttachementsFromActivity(activityId, removeNotes));
    }

    @ApiOperation(value = "Get All Activities by unitId")
    @GetMapping(value = "/activity")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivityByUnitId( @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                activityService.getActivityByUnitId(unitId));
    }

    @ApiOperation(value = "Get All Activities and Phases by unitId")
    @GetMapping(value = "/activity_with_phase")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivityAndPhaseByUnitId( @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getActivityAndPhaseByUnitId(unitId));
    }

    @ApiOperation("Get all unit activities with categories ")
    @GetMapping(value = "/activities_categories")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivitiesWithCategories(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getActivitiesWithCategories(unitId));
    }

    @ApiOperation("Get all absence activity")
    @GetMapping(value = "/absence-activities")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllAbsenceActivities(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getAllAbsenceActivity(unitId));
    }

    @ApiOperation("Get all children of Activity")
    @PutMapping(value = "/activity/get_all_Children")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getAllChildren(@RequestBody Set<BigInteger> activityIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getAllChildren(activityIds));
    }

    @ApiOperation("Get all activity ranking")
    @GetMapping(value = "/get_activity_rank")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivityRankWithRankByUnitId(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getActivityRankWithRankByUnitId(unitId));
    }

    @ApiOperation("Get all activity details with priority")
    @GetMapping(value = "/get_activity_details_with_priority")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getActivityDetailsWithRankByUnitId(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getActivityDetailsWithRankByUnitId(unitId));
    }


}
