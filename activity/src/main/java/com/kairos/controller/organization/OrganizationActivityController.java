package com.kairos.controller.organization;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.CommunicationActivityDTO;
import com.kairos.dto.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.activity.tabs.ActivityOptaPlannerSetting;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.organization.OrganizationActivityService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import static com.kairos.commons.utils.ObjectUtils.newHashSet;
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
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','ADD')")
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
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> addIconInActivity(@PathVariable Long unitId,@PathVariable BigInteger activityId, @RequestParam("file") MultipartFile file) throws
            IOException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.addIconInActivity(unitId,activityId, file));
    }

    @ApiOperation("Update ActivityGeneralSettings Tab of Activity")
    @PutMapping(value = "/activity/general")
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
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
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> updateLanguageSettings(@NotEmpty  @PathVariable Long unitId, @NotEmpty @PathVariable BigInteger activityId, @NotNull @RequestBody Map<String, TranslationInfo> translationMap) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.updateUnitActivityTranslationDetails(activityId,unitId,translationMap));
    }


    @ApiOperation("get Rules Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/rules")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getRulesTab(@PathVariable BigInteger activityId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getRulesTabOfActivity(activityId, unitId));
    }

    @ApiOperation("Update Rules Tab of Activity")
    @PutMapping(value = "/activity/rules")
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> updateRulesTab(@RequestBody ActivityRulesSettingsDTO rulesDTO,@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateRulesTab(unitId,rulesDTO,true));
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
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> updatePhaseSetticopyngTab(@PathVariable Long unitId,@RequestBody ActivityPhaseSettings activityPhaseSettings) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updatePhaseSettingTab(unitId,activityPhaseSettings));
    }

    @ApiOperation("get getTime Calculation Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/timeCalculation")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getTimeCalculationTabOfActivity(@PathVariable BigInteger activityId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getTimeCalculationTabOfActivity(activityId, unitId));
    }

    @ApiOperation("Update Time calculation Tab of Activity")
    @PutMapping(value = "/activity/timeCalculation")
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> updateTimeCalculationTabOfActivity(@PathVariable Long unitId,@RequestBody TimeCalculationActivityDTO timeCalculationActivityDTO,@RequestParam boolean availableAllowActivity) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateTimeCalculationTabOfActivity(unitId,timeCalculationActivityDTO,availableAllowActivity));
    }


    @ApiOperation("Update IndividualPoints Tab of Activity")
    @PutMapping(value = "/activity/individualPoints")
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> updateIndividualPointsTab(@PathVariable Long unitId,@RequestBody ActivityIndividualPointsSettingsDTO individualPointsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateIndividualPointsTab(unitId,individualPointsDTO));
    }

    @ApiOperation("get IndividualPoints Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/individualPoints")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getIndividualPointsTab(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getIndividualPointsTabOfActivity(activityId));
    }


    @ApiOperation("delete an activity")
    @DeleteMapping(value = "/activity/{activityId}")
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','DELETE')")
    public ResponseEntity<Map<String, Object>> deleteActivity(@PathVariable Long unitId,@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.deleteActivity(unitId,activityId));
    }

    @ApiOperation("Update child Activivty of Activity")
    @PutMapping(value = "/activity/{activityId}/child_activities")
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> assignChildActivitiesInActivity(@PathVariable Long unitId,@PathVariable BigInteger activityId, @RequestBody Set<BigInteger> childActivitiesIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.assignChildActivitiesInActivity(unitId,activityId,childActivitiesIds));
    }

    @ApiOperation("get compositeShifts Tab of Activity")
    @GetMapping(value = "/activity/{activityId}/allowed_child_activities")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> getCompositeShiftTabOfActivity(@PathVariable BigInteger activityId,@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.getCompositeAndChildActivityOfUnitActivity(activityId,unitId));
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
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> addDocumentInNotesTab(@PathVariable Long unitId,@PathVariable BigInteger activityId, @RequestParam("file") MultipartFile file) throws
            IOException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.addDocumentInNotesTab(unitId, activityId, file));
    }

    @ApiOperation("Update Communication Tab of Activity")
    @PutMapping(value = "/activity/communication")
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> updateCommunicationTabOfActivity(@PathVariable Long unitId,@RequestBody CommunicationActivityDTO communicationActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.updateCommunicationTabOfActivity(unitId,communicationActivityDTO, true));
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
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> updateBonusTabOfActivity(@PathVariable Long unitId,@RequestBody BonusActivityDTO bonusActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateBonusTabOfActivity(unitId,bonusActivityDTO));
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
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> updateSkillTabOfActivity(@PathVariable Long unitId,@RequestBody SkillActivityDTO skillActivityDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateSkillTabOfActivity(unitId,skillActivityDTO));
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
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> updateOrgMappingDetailOfActivity(@PathVariable Long unitId,@PathVariable BigInteger activityId, @RequestBody ActivityOptaPlannerSetting activityOptaPlannerSetting) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateOptaPlannerSettingsTabOfActivity(unitId,activityId, activityOptaPlannerSetting));
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
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> updateCtaAndWtaSettingsTabOfActivity(@PathVariable Long unitId,@RequestBody ActivityCTAAndWTASettingsDTO activityCTAAndWTASettingsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateCtaAndWtaSettingsTabOfActivity(unitId,activityCTAAndWTASettingsDTO));
    }

    @ApiOperation("update organization Mapping details  of activity Type")
    @PutMapping(value = "/activity/{activityId}/organizationMapping")
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> updateOrgMappingDetailOfActivity(@PathVariable Long unitId,@RequestBody OrganizationMappingDTO organizationMappingDTO, @PathVariable BigInteger activityId) {
        activityService.updateOrgMappingDetailOfActivity(unitId,organizationMappingDTO, activityId);
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
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','ADD')")
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
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> updateLocationsTabOfActivity(@PathVariable Long unitId,@RequestBody ActivityLocationSettingsDTO activityLocationSettingsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.updateLocationsTabOfActivity(unitId,activityLocationSettingsDTO));
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
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> removeAttachementsFromActivity(@PathVariable Long unitId,@PathVariable BigInteger activityId, @RequestParam boolean removeNotes) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.removeAttachementsFromActivity(unitId,activityId, removeNotes));
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
    public ResponseEntity<Map<String, Object>> getActivityAndPhaseByUnitId( @PathVariable Long unitId) {
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
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getAllAbsenceActivity(unitId));
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

    @ApiOperation("Get all on call And stand by activityIds ")
    @GetMapping(value = "/get_on_call_and_stand_by_activitIds")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getShowOnCallAndStandByActivityId(@PathVariable long unitId,@RequestParam boolean showStandBy,@RequestParam boolean showOnCall) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.getShowOnCallAndStandByActivityId(unitId,showStandBy,showOnCall));
    }

    @ApiOperation("Update Translations of ActivityCategory in a unit")
    @PutMapping(value = "/activityCategory/{activityCategoryId}/unit_language_settings")
    @PreAuthorize("@appPermissionEvaluator.isValid('Activity','EDIT')")
    ResponseEntity<Map<String, Object>> updateLanguageSettingsOfActivityCategory(@PathVariable Long unitId,@NotEmpty @PathVariable BigInteger activityCategoryId, @NotNull @RequestBody Map<String, TranslationInfo> translationMap) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationActivityService.updateUnitActivityCategoryTranslationDetails(activityCategoryId,translationMap));
    }

    @ApiOperation("Get all activities by time type in a unit")
    @GetMapping(value = "/all_activities")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllActivitiesByTimeType(@PathVariable long unitId, @RequestParam TimeTypeEnum timeType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, activityService.findAllBySecondLevelTimeTypeAndUnitIds(timeType, newHashSet(unitId)));
    }
}
