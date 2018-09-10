package com.kairos.controller.organization;

import com.kairos.dto.activity.activity.OrganizationMappingActivityTypeDTO;
import com.kairos.dto.user.organization.*;
import com.kairos.persistence.model.client.ClientStaffDTO;
import com.kairos.persistence.model.organization.OpeningHours;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationGeneral;
import com.kairos.persistence.model.organization.UnitManagerDTO;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.organization.team.TeamDTO;
import com.kairos.persistence.model.staff.StaffFilterDTO;
import com.kairos.persistence.model.user.department.Department;
import com.kairos.persistence.model.user.resources.ResourceDTO;
import com.kairos.persistence.model.user.resources.ResourceUnavailabilityDTO;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.tpa_services.IntegrationConfiguration;
import com.kairos.service.client.ClientBatchService;
import com.kairos.service.client.ClientService;
import com.kairos.service.client.VRPClientService;
import com.kairos.service.country.CountryService;
import com.kairos.service.country.EmploymentTypeService;
import com.kairos.service.language.LanguageService;
import com.kairos.service.organization.*;
import com.kairos.service.region.RegionService;
import com.kairos.service.resources.ResourceService;
import com.kairos.service.skill.SkillCategoryService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.staff.StaffService;
import com.kairos.service.tpa_services.IntegrationConfigurationService;
import com.kairos.service.unit_position.UnitPositionService;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotsDeductionDTO;

import com.kairos.dto.user.staff.client.ClientFilterDTO;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.utils.external_plateform_shift.GetWorkShiftsFromWorkPlaceByIdResult;
import com.kairos.utils.user_context.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;
import static com.kairos.constants.ApiConstants.UNIT_URL;


/**
 * OrganizationController
 * 1.Calls Organization Service
 * 2. Call for CRUD operation on Organization using OrganizationService.
 */
@RestController

@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class OrganizationController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private OrganizationService organizationService;
    @Inject private CompanyCreationService companyCreationService;
    @Inject
    private SkillCategoryService skillCategoryService;
    @Inject
    private OrganizationServiceService organizationServiceService;
    @Inject
    private SkillService skillService;
    @Inject
    private TimeSlotService timeSlotService;
    @Inject
    private RegionService regionService;
    @Inject
    private IntegrationConfigurationService integrationConfigurationService;
    @Inject
    private ClientService clientService;
    @Inject
    private OpenningHourService openningHourService;
    @Inject
    private GroupService groupService;
    @Inject
    private TeamService teamService;
    @Inject
    private ResourceService resourceService;
    @Inject
    private OrganizationAddressService organizationAddressService;
    @Inject
    private DepartmentService departmentService;
    @Inject
    private OrganizationHierarchyService organizationHierarchyService;
    @Inject
    private StaffService staffService;
    @Inject
    private LanguageService languageService;
    @Inject
    private ClientBatchService clientBatchService;
    @Inject
    private CountryService countryService;
    @Inject
    private EmploymentTypeService employmentTypeService;
    @Inject
    private UnitPositionService unitPositionService;
    @Inject private VRPClientService vrpClientService;

    /**
     * @return List of Organization- All organization in db.
     */
    @ApiOperation(value = "Get all Organization")
    @RequestMapping(method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public List<Map<String, Object>> getAllOrganization() {
        return organizationService.getAllOrganization();
    }


    /**
     * Return Organization with given id and return if found.
     *
     * @return Organization
     */
    @ApiOperation(value = "Get Organization by Id")
    @RequestMapping(value = UNIT_URL, method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationById(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getOrganizationById(unitId));
    }

    @ApiOperation(value = "Get Organization with countryId")
    @RequestMapping(value = UNIT_URL+"/getOrganisationWithCountryId", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationWithCountryId(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getOrganizationWithCountryId(unitId));
    }

    @ApiOperation(value = "Get Organization's showCountryTag setting by Id")
    @RequestMapping(value = UNIT_URL + "/show_country_tags", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> showCountryTagForOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.showCountryTagForOrganization(unitId));
    }

    @ApiOperation(value = "Get Organization's country Id")
    @RequestMapping(value = UNIT_URL + "/countryId", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryIdOfOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getCountryIdOfOrganization(unitId));
    }

    //TODO

    /**
     * Return Organization with given id and return if found.
     *
     * @return Organization
     */
    @ApiOperation(value = "Get Organization by Id")
    @RequestMapping(value = UNIT_URL + "/WithoutAuth", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationWithoutAuth(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getOrganizationById(unitId));
    }

    /**
     * Create new organization in db and return created organization
     *
     * @param organizationBasicDTO
     * @return Organization
     */

    /**
     *
     */
    @ApiOperation(value = "Get a Organization(Location)")
    @GetMapping(value = UNIT_URL + "/unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getUnits(unitId));
    }

    /**
     *
     */
    @ApiOperation(value = "Get organization herirchy data")
    @GetMapping(value = UNIT_URL + "/manage_hierarchy")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getManageHierarchyData(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getManageHierarchyData(unitId));
    }

    /**
     * assign staff to departments
     *
     * @param organizationId
     * @param departmentId
     * @param staff
     * @return
     */
    @ApiOperation(value = "add staff in department")
    @RequestMapping(value = "/{organizationId}/department/{departmentId}/staff", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createStaff(@PathVariable Long organizationId, @PathVariable Long departmentId, @RequestBody Map<String, String> staff) {
        if (organizationId != null) {
            long userId = Long.parseLong(staff.get("userId"));
            return ResponseHandler.generateResponse(HttpStatus.OK, true, departmentService.addStaff(organizationId, departmentId, userId));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }


    /**
     * to create department in organization
     *
     * @param organizationId
     * @param department
     * @return
     */

    @ApiOperation(value = "add department in organization")
    @RequestMapping(value = "/{organizationId}/department", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createDepartment(@PathVariable Long organizationId, @RequestBody Department department) {
        if (organizationId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, departmentService.createDepartment(organizationId, department));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @ApiOperation(value = "Get department in organization")
    @RequestMapping(value = "/{organizationId}/department", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDepartment(@PathVariable Long organizationId) {
        if (organizationId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, departmentService.getDepartment(organizationId));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @ApiOperation(value = "Get Department Accessible in organization")
    @RequestMapping(value = "/department/{departmentId}/accessible", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDepartmentAccessibleOrganization(@PathVariable Long departmentId) {
        if (departmentId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, departmentService.getDepartmentAccessibleOrganizations(departmentId));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    /**
     * group/
     * this method will link departments with teams or other organization,
     * super admin will give permission to department to manage further child organizations
     *
     * @param departmentId
     * @param map          // this will contain ids of child organizations
     */
    @ApiOperation(value = "add staff in department")
    @RequestMapping(value = "/department/{departmentId}/manage", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public void manageOrganizationStructure(@PathVariable Long departmentId, @RequestBody Map<String, Object> map) {

        List<Long> childIds = (List<Long>) map.get("childIds");
        departmentService.manageStructure(departmentId, childIds);
    }

    @ApiOperation(value = "Get skills of organization")
    @RequestMapping(value = "/unit/{unitId}/skill", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationAvailableSkills(@PathVariable long unitId, @RequestParam("type") String type) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                skillService.getAllAvailableSkills(unitId, type));
    }

    @ApiOperation(value = "Get Unit Data of organization")
    @RequestMapping(value = "/unit/{unitId}/unitData", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnitData(@PathVariable long unitId, @RequestParam("type") String type) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                skillService.getUnitData(unitId));
    }

    @ApiOperation(value = "Add Organization Skills One by One")
    @RequestMapping(value = "/unit/{unitId}/skill", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addOrganizationSkills(@PathVariable long unitId, @RequestBody Map<String, Object> data, @RequestParam("type") String type) {

        long skillId = Long.valueOf(String.valueOf(data.get("id")));
        boolean isSelected = (boolean) data.get("isSelected");
        String visitourId = (String) data.get("visitourId");
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                skillService.addNewSkill(unitId, skillId, isSelected, type, visitourId));
    }

    @ApiOperation(value = "update skill(visitour, custom Name) for an organization")
    @RequestMapping(value = "/unit/{unitId}/skill/{skillId}/visitour_details", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateSkillOfOrganization(@PathVariable long unitId, @PathVariable long skillId, @RequestParam("type") String type, @Valid @RequestBody OrganizationSkillDTO organizationSkillDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillService.updateSkillOfOrganization(unitId, skillId, type, organizationSkillDTO));
    }

    @ApiOperation(value = "get skills of staff")
    @RequestMapping(value = "/unit/{unitId}/staff/skills", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffSkills(@RequestParam("type") String type, @PathVariable long unitId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillService.getStaffSkills(unitId, type));
    }


    @ApiOperation(value = "assign skill to staff")
    @RequestMapping(value = "/unit/{unitId}/skill/{skillId}/assign", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> assignSkillToStaff(@PathVariable long skillId, @RequestParam("type") String type, @PathVariable long unitId, @RequestBody Map<String, Object> data) {


        long staffId = Long.valueOf((String) data.get("staffId"));
        boolean isSelected = (boolean) data.get("isSelected");
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillService.assignSkillToStaff(unitId, staffId, skillId, isSelected, type));
    }


    // Service
    @ApiOperation(value = "Get Available Services")
    @RequestMapping(value = "unit/{unitId}/service/data", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationServiceData(@PathVariable long organizationId, @PathVariable long unitId, @RequestParam("type") String type) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationServiceService.organizationServiceData(unitId, type));
    }

    @ApiOperation(value = "Add and Remove Available Services")
    @RequestMapping(value = "/unit/{unitId}/service", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addOrganizationService(@PathVariable long unitId, @RequestBody Map<String, Object> data, @RequestParam("type") String type) {
        long organizationServiceId = Long.valueOf(String.valueOf(data.get("organizationServiceId")));
        boolean isSelected = (boolean) data.get("isSelected");

        Map<String, Object> services = organizationServiceService.updateServiceToOrganization(unitId, organizationServiceId, isSelected, type);
        if (services == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, true, false);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, services);

    }


    @ApiOperation(value = "Get Organization Time Slots")
    @RequestMapping(value = "/unit/{unitId}/time_slot", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTimeSlots(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getTimeSlots(unitId));
    }

    @ApiOperation(value = "Get Organization Time Slots")
    @RequestMapping(value = "/unit/{unitId}/time_slot_set/{timeSlotSetId}/time_slot", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTimeSlots(@PathVariable Long timeSlotSetId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getTimeSlotByTimeSlotSet(timeSlotSetId));
    }

    @ApiOperation(value = "Get Organization Time Slot sets")
    @RequestMapping(value = "/unit/{unitId}/time_slot_set", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTimeSlotSets(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getTimeSlotSets(unitId));
    }

    @ApiOperation(value = "create new time slot set")
    @RequestMapping(value = "/unit/{unitId}/time_slot", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createTimeSlotSet(@PathVariable long unitId, @Validated @RequestBody TimeSlotSetDTO timeSlotSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.createTimeSlotSet(unitId, timeSlotSetDTO));
    }

    @ApiOperation(value = "create new time slot set")
    @RequestMapping(value = "/unit/{unitId}/time_slot_set/{timeSlotSetId}/time_slot", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createTimeSlot(@PathVariable Long timeSlotSetId,
                                                              @Validated @RequestBody TimeSlotDTO timeSlotDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.createTimeSlot(timeSlotSetId, timeSlotDTO));
    }

    @ApiOperation(value = "delete time slot set")
    @RequestMapping(value = "/unit/{unitId}/time_slot_set/{timeSlotId}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteTimeSlotSet(@PathVariable Long unitId, @PathVariable Long timeSlotId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.deleteTimeSlotSet(unitId, timeSlotId));
    }

    @ApiOperation(value = "update time slot set")
    @RequestMapping(value = "/unit/{unitId}/time_slot_set/{timeSlotSetId}", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updateTimeSlotSet(@PathVariable Long unitId, @PathVariable Long timeSlotSetId,
                                                                 @Validated @RequestBody TimeSlotSetDTO timeSlotSetDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.updateTimeSlotSet(unitId, timeSlotSetId, timeSlotSetDTO));
    }

    @ApiOperation(value = "update time slot type")
    @RequestMapping(value = "/unit/{unitId}/time_slot_type", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTimeSlotType(@PathVariable long unitId, @RequestBody Map<String, Object> timeSlotType) {
        boolean standardTimeSlot = (boolean) timeSlotType.get("standardTimeSlot");
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.updateTimeSlotType(unitId, standardTimeSlot));
    }


    @ApiOperation(value = "Update time slot")
    @RequestMapping(value = "/unit/{unitId}/time_slot_set/{timeSlotId}/time_slot", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTimeSlot(@Validated @RequestBody List<TimeSlotDTO> timeSlotDTO,
                                                              @PathVariable Long timeSlotId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.updateTimeSlot(timeSlotDTO, timeSlotId));
    }

    @ApiOperation(value = "Delete time slot")
    @RequestMapping(value = "/unit/{unitId}/time_slot_set/{timeSlotSetId}/time_slot/{timeSlotId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteTimeSlot(@PathVariable long timeSlotId, @PathVariable Long timeSlotSetId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.deleteTimeSlot(timeSlotId, timeSlotSetId));
    }

    @ApiOperation(value = "Get Organization Hierarchy")
    @RequestMapping(value = "/hierarchy", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationHierarchy(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationHierarchyService.generateHierarchyMinimum(organizationId));
    }

    @ApiOperation(value = "Get Organization Hierarchy")
    @RequestMapping(value = "/organization_flow/hierarchy", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationHierarchyForOrganizationTab(@PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationHierarchyService.generateHierarchy(organizationId));
    }

    @ApiOperation(value = "Get Organization Clients with min details")
    @RequestMapping(value = "/unit/{unitId}/client", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationClients(@PathVariable Long organizationId, @PathVariable Long unitId)  throws InterruptedException, ExecutionException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientService.getOrganizationClients(unitId));
    }

    @ApiOperation(value = "Get Organization Clients with min details")
    @RequestMapping(value = "/unit/{unitId}/client/planner", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationClientsWithPlanning(@PathVariable Long organizationId, @PathVariable Long unitId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientService.getOrganizationClientsWithPlanning(unitId));
    }

    @ApiOperation(value = "Get Organization Clients with max details")
    @RequestMapping(value = "/unit/{unitId}/client/all", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationAllClients(@PathVariable long organizationId, @PathVariable long unitId) {
        long userId = UserContext.getUserDetails().getId();
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientService.getOrganizationAllClients(organizationId, unitId, userId));
    }

    @RequestMapping(value = "/unit/{unitId}/client/upload", method = RequestMethod.POST)
    @ApiOperation("Upload XLSX file ")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> batchCreateClient(@PathVariable long unitId, @RequestParam("file") MultipartFile multipartFile) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientBatchService.batchAddClientsToDatabase(multipartFile, unitId));
    }

    @RequestMapping(value = "/staff/available/{organizationId}", method = RequestMethod.GET)
    @ApiOperation("Get uploaded Staff as per orgnaizationID ")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffByOrganizationId(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffService.getUploadedStaffByOrganizationId(organizationId));
    }

    @RequestMapping(value = "/unit/{unitId}/general", method = RequestMethod.GET)
    @ApiOperation("Get general details of Client")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getGeneralDetails(@PathVariable long unitId, @RequestParam("type") String type) {
        Map<String, Object> objectMap = organizationService.getGeneralDetails(unitId, type);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, objectMap);
    }

    @RequestMapping(value = "/unit/{unitId}/general", method = RequestMethod.PUT)
    @ApiOperation("Update general details of Client")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOrganizationGeneralDetails(@PathVariable long unitId, @Validated @RequestBody OrganizationGeneral organizationGeneral) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.updateOrganizationGeneralDetails(organizationGeneral, unitId));
    }

    @RequestMapping(value = "/unit/{unitId}/group/general", method = RequestMethod.PUT)
    @ApiOperation("update general details of group")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateGroupGeneralDetails(@PathVariable long unitId, @Validated @RequestBody Group group) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                groupService.updateGroupGeneralDetails(unitId, group));
    }

    @RequestMapping(value = "/unit/{unitId}/team/general", method = RequestMethod.PUT)
    @ApiOperation("update general details of team")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTeamGeneralDetails(@PathVariable long unitId, @Validated @RequestBody TeamDTO teamDTO) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                teamService.updateTeamGeneralDetails(unitId, teamDTO));
    }

    @RequestMapping(value = "unit/{unitId}/teams", method = RequestMethod.GET)
    @ApiOperation("Get Team of a Unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnitTeams(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                teamService.getTeamsInUnit(unitId));
    }

    @RequestMapping(value = "unit/{unitId}/languages", method = RequestMethod.GET)
    @ApiOperation("Update Opening hour details")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getLanguages(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, languageService.getUnitAvailableLanguages(unitId));
    }

    @RequestMapping(value = "unit/{unitId}/setting/opening_hours", method = RequestMethod.PUT)
    @ApiOperation("Update Opening hour details")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOpeningHoursDetails(@RequestBody OpeningHours openingHours) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openningHourService.updateOpeningHoursDetails(openingHours));
    }

    @RequestMapping(value = "unit/{unitId}/setting", method = RequestMethod.GET)
    @ApiOperation("Get Unit opening hours")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOpeningHoursDetails(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openningHourService.getOpeningHoursAndHolidayDetails(unitId));
    }


    @RequestMapping(value = "unit/{unitId}/setting/holidays", method = RequestMethod.GET)
    @ApiOperation("Get Unit opening hours")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getHolidays(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                openningHourService.getOrganizationHolidays(unitId));
    }

    @RequestMapping(value = "/parent/{countryId}", method = RequestMethod.GET)
    //@ApiOperation("Get Parent Organization")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getParentOrganization(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getParentOrganization(countryId));
    }
/**
 * Its fetching all the units of the  organization
 *
 * */
    @RequestMapping(value = "/parent/{orgId}/country/{countryId}/gdpr_workcenter", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getOrganizationGdprAndWorkcenter(@PathVariable long orgId,@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getOrganizationGdprAndWorkcenter(orgId, countryId));
    }

    @RequestMapping(value = "/unit", method = RequestMethod.GET)
    @ApiOperation("get child units of parent organization")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getChildUnits(@PathVariable long organizationId, @RequestParam(value = "moduleId") String moduleId,
                                                             @RequestParam(value = "userId") long userId) {

        //TODO there is hardcoded module id,later will get from url @prabjot
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationHierarchyService.getChildUnits(organizationId, userId, moduleId));
    }

    @RequestMapping(value = "/unit/{unitId}/resources", method = RequestMethod.GET)
    @ApiOperation("Get Organization Resource of a Unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationResources(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                resourceService.getUnitResources(unitId));
    }

    @RequestMapping(value = "/unit/{unitId}/resources_with_unavailability", method = RequestMethod.GET)
    @ApiOperation("Get Organization Resource of a Unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationResourcesWithUnAvailability(@PathVariable Long unitId,
                                                                                          @RequestParam("startDate") String date) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                resourceService.getOrganizationResourcesWithUnAvailability(unitId, date));
    }


    @RequestMapping(value = "/unit/{unitId}/resources/type", method = RequestMethod.GET)
    @ApiOperation("Get Organization Resource Type Array")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationResourcesTypes(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                resourceService.getUnitResourcesTypes(unitId));
    }

    @RequestMapping(value = "/unit/{unitId}/resources", method = RequestMethod.POST)
    @ApiOperation("Update Resource of a Unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createResourceForOrganization(@PathVariable Long unitId,
                                                                             @Valid @RequestBody ResourceDTO resourceDTO) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                resourceService.addResource(resourceDTO, unitId));
    }

    @RequestMapping(value = "/unit/{unitId}/resource/{resourceId}/unavailability", method = RequestMethod.POST)
    @ApiOperation("set resource unavailability")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> setResourceUnavailability(@PathVariable Long resourceId,
                                                                         @Valid @RequestBody ResourceUnavailabilityDTO unavailabilityDTO) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                resourceService.setResourceUnavailability(unavailabilityDTO, resourceId));
    }

    @RequestMapping(value = "/unit/{unitId}/resource/{resourceId}/unavailability/{unavailabilityId}", method = RequestMethod.PUT)
    @ApiOperation("get resource unavailability")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getResourceUnavailability(@PathVariable Long resourceId,
                                                                         @PathVariable Long unavailabilityId,
                                                                         @Valid @RequestBody ResourceUnavailabilityDTO unavailabilityDTO) throws ParseException {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, resourceService.
                updateResourceUnavailability(unavailabilityDTO, unavailabilityId, resourceId));
    }

    @RequestMapping(value = "/unit/{unitId}/resource/{resourceId}/unavailability/{unavailableDateId}", method = RequestMethod.DELETE)
    @ApiOperation("delete resource unavailability")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteResourceUnavailability(@PathVariable Long resourceId, @PathVariable Long unavailableDateId) {
        resourceService.deleteUnavailability(resourceId, unavailableDateId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @RequestMapping(value = "/unit/{unitId}/resource/{resourceId}/unavailability", method = RequestMethod.GET)
    @ApiOperation("get resource unavailability")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getResourceUnavailability(@PathVariable Long resourceId, @RequestParam("startDate") String date) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, resourceService.getResourceUnAvailability(resourceId, date));
    }


    @RequestMapping(value = "/unit/{unitId}/resource/{resourceId}", method = RequestMethod.PUT)
    @ApiOperation("Update Resource of a Unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateResource(@PathVariable Long resourceId,
                                                              @Valid @RequestBody ResourceDTO resourceDTO) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                resourceService.updateResource(resourceDTO, resourceId));

    }

    /**
     * Mark disabled to a resource by given id
     *
     * @param resourceId
     */
    @RequestMapping(value = "/unit/{unitId}/resource/{resourceId}", method = RequestMethod.DELETE)
    @ApiOperation("Delete a resource by resourceId")
    ResponseEntity<Map<String, Object>> deleteResourceById(@PathVariable Long resourceId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, resourceService.deleteResource(resourceId));
    }


    @RequestMapping(value = "unit/{unitId}/unit_manager", method = RequestMethod.POST)
    @ApiOperation("create unit manager")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createUnitManager(@PathVariable long unitId, @Validated @RequestBody com.kairos.persistence.model.organization.UnitManagerDTO unitManagerDTO) {
        Map response = staffService.createUnitManager(unitId, unitManagerDTO);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.CONFLICT, true,
                    false);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                response);
    }


    @RequestMapping(value = "unit/{unitId}/unit_manager", method = RequestMethod.GET)
    @ApiOperation("get unit manager")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnitManager(@PathVariable long organizationId, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffService.getUnitManager(unitId));
    }

    @RequestMapping(value = UNIT_URL + "/address", method = RequestMethod.GET)
    @ApiOperation("get location of organization")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAddress(@PathVariable long unitId, @RequestParam("type") String type) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationAddressService.getAddress(unitId, type));
    }

    @RequestMapping(value = UNIT_URL + "/zipcode/{zipCodeId}/address", method = RequestMethod.GET)
    @ApiOperation("get location of organization")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAddressByZipCode(@PathVariable long zipCodeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                regionService.getAllZipCodesData(zipCodeId));
    }


    @RequestMapping(value = UNIT_URL + "/contact_address", method = RequestMethod.PUT)
    @ApiOperation("Update contact address of a Unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateContactAddress(@PathVariable long unitId, @Validated @RequestBody AddressDTO address, @RequestParam("type") String type) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationAddressService.updateContactAddressOfUnit(address, unitId, type));
    }

    @RequestMapping(value = UNIT_URL + "/billing_address", method = RequestMethod.PUT)
    @ApiOperation("Update billing address of unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateBillingAddress(@PathVariable long unitId, @RequestBody AddressDTO addressDetails) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationAddressService.saveBillingAddress(addressDetails, unitId, true));
    }

    @RequestMapping(value = UNIT_URL + "/billing_address", method = RequestMethod.POST)
    @ApiOperation("save  billing address of unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveBillingAddress(@PathVariable long unitId, @RequestBody AddressDTO addressDetails) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationAddressService.saveBillingAddress(addressDetails, unitId, false));
    }

    @RequestMapping(value = "unit/{unitId}/addContactAddress", method = RequestMethod.PUT)
    @ApiOperation("Update Team of a Unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addUnitAddress(@PathVariable Long unitId, @RequestBody Map<String, Object> contactAddress) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationAddressService.addUnitAddress(unitId, contactAddress));
    }

    @RequestMapping(value = "unit/{unitId}/deleteChildOrganization", method = RequestMethod.DELETE)
    @ApiOperation("Permanent Delete organization node, don't invoke this method")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteOrganizationById(@PathVariable Long organizationId, @PathVariable Long unitId) {
        Boolean status = organizationService.deleteOrganizationById(organizationId, unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                status);
    }

    @RequestMapping(value = UNIT_URL + "/request/skill_create", method = RequestMethod.POST)
    @ApiOperation("request admin to create new skill")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> requestForCreateNewSkill(@PathVariable long unitId, @RequestBody Skill skill) {

        if (skillService.requestForCreateNewSkill(unitId, skill)) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true,
                    true);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false,
                false);
    }

    @ApiOperation(value = "Get integration services")
    @RequestMapping(value = "/unit/{unitId}/integration_service", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getIntegrationServices() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationConfigurationService.getAllIntegrationServices());
    }

    @ApiOperation(value = "Add integration service")
    @RequestMapping(value = "/integration_service", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addIntegrationService(@Validated @RequestBody IntegrationConfiguration objectToSave) {
        HashMap<String, Object> integrationConfiguration = integrationConfigurationService.addIntegrationConfiguration(objectToSave);
        if (integrationConfiguration == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, integrationConfiguration);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationConfiguration);
    }

    @ApiOperation(value = "Update integration service")
    @RequestMapping(value = "/integration_service/{integrationServiceId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateIntegrationService(@Validated @RequestBody IntegrationConfiguration integrationConfiguration, @PathVariable long integrationServiceId) {
        HashMap<String, Object> updatedObject = integrationConfigurationService.updateIntegrationService(integrationServiceId, integrationConfiguration);
        if (updatedObject == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, updatedObject);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, updatedObject);
    }

    @ApiOperation(value = "Delete integration service")
    @RequestMapping(value = "/integration_service/{integrationServiceId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteIntegrationService(@PathVariable long integrationServiceId) {
        boolean isDeleted = integrationConfigurationService.deleteIntegrationService(integrationServiceId);
        if (isDeleted) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, integrationConfigurationService.deleteIntegrationService(integrationServiceId));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);

    }

    @ApiOperation(value = "Update Organization External Id")
    @RequestMapping(value = "/setExternalId/{externalId}", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOrganizationExternalId(@PathVariable long organizationId, @PathVariable long externalId) {
        Organization organization = organizationService.updateExternalId(organizationId, externalId);
        return ResponseHandler.generateResponse(HttpStatus.OK, false, organization);

    }

    @ApiOperation(value = "Update Estimote credentials")
    @RequestMapping(value = "/unit/{unitId}/estimote_credentials", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOrganizationEstimoteCredentials(@PathVariable(value = "unitId") long unitId, @RequestBody Map<String, String> payload) {

        Map organization = organizationService.setEstimoteCredentials(unitId, payload);
        if (organization == null)
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, organization);
        return ResponseHandler.generateResponse(HttpStatus.OK, false, organization);

    }

    @ApiOperation(value = "GET Estimote credentials")
    @RequestMapping(value = "/unit/{unitId}/estimote_credentials", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationEstimoteCredentials(@PathVariable(value = "unitId") long unitId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, false, organizationService.getEstimoteCredentials(unitId));
    }

    @ApiOperation(value = "Create Link between parent and child")
    @RequestMapping(value = "/unit/{unitId}/child/{childId}", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createLinkParentWithChildOrganization(@PathVariable(value = "unitId") long unitId, @PathVariable(value = "childId") long childId) {

        Boolean status = organizationService.createLinkParentWithChildOrganization(unitId, childId);

        return ResponseHandler.generateResponse(HttpStatus.OK, false, status);

    }

    @ApiOperation("Assign staff to citizen")
    @RequestMapping(value = "/unit/{unitId}/client/assign/staff", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> assignStaffToCitizen(@RequestBody ClientStaffDTO clientStaffDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.assignStaffToCitizen(clientStaffDTO.getCitizenId(), clientStaffDTO.getStaffId(), clientStaffDTO.getType()));
    }

    @ApiOperation("Assign staff to citizen")
    @RequestMapping(value = "/unit/{unitId}/client/assign/bulk/staff", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> assignMultipleStaffToClient(@PathVariable long unitId, @RequestBody ClientStaffDTO clientStaffDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.assignMultipleStaffToClient(unitId, clientStaffDTO.getType()));
    }

    @ApiOperation("get assigned staff to citizen")
    @RequestMapping(value = "/unit/{unitId}/client/assign/staff", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAssignedStaffOfCitizen(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clientService.getAssignedStaffOfCitizen(unitId));
    }

    @RequestMapping(value = "unit/{unitId}/unit_manager/{staffId}", method = RequestMethod.PUT)
    @ApiOperation("create unit manager")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateUnitManager(@PathVariable long staffId, @Validated @RequestBody UnitManagerDTO unitManagerDTO) {
        UnitManagerDTO response = staffService.updateUnitManager(staffId, unitManagerDTO);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.CONFLICT, true,
                    false);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                response);
    }

    // Service
    @ApiOperation(value = "Get Imported Services")
    @RequestMapping(value = "unit/{unitId}/importedService/data", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationImportedServiceData(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationServiceService.organizationImportedServiceData(unitId));
    }


    // Service
    @ApiOperation(value = "Map Imported Services")
    @RequestMapping(value = "unit/{unitId}/mapImportedService/{imPortedServiceId}/service/{serviceId}", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> mapImportedService(@PathVariable long imPortedServiceId, @PathVariable long serviceId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationServiceService.mapImportedService(imPortedServiceId, serviceId));
    }


    /**
     * @param unitId
     * @return
     * @auther anil maurya
     * this endpoint is called from task micro service
     */
    @ApiOperation("get assigned staff to citizen")
    @RequestMapping(value = "/unit/{unitId}/common_data", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getCommonDataOfOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getCommonDataOfOrganization(unitId));
    }


    /**
     * @param unitId
     * @return
     * @auther anil maurya
     * this endpoint is called from task micro service
     */
    @ApiOperation("get visitation info for a unit")
    @RequestMapping(value = "/unit/{unitId}/unit_visitation", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getUnitVisitationInfo(@PathVariable Long organizationId, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getUnitVisitationInfo(organizationId, unitId));
    }

    /**
     * this url will be call from rest template,
     * it provides skills of organization for task type tab
     *
     * @param unitId
     * @return
     */
    @ApiOperation(value = "Get skills of organization")
    @RequestMapping(value = "/unit/{unitId}/skills", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getSkillsOfOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                skillService.getSkillsOfOrganization(unitId));
    }

    /**
     * this url will be called by using rest template
     * provides current time slots
     *
     * @param unitId
     * @return
     */
    @ApiOperation(value = "Get current time slots of organization")
    @RequestMapping(value = "/unit/{unitId}/current/time_slots", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getCurrentTimeSlotsOfOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                timeSlotService.getCurrentTimeSlotOfUnit(unitId));
    }

    /*
     * This endpoint in called from task micro service to get time slot info by unit id and time slot name
     * @param unitId
     * @param timeSlotName
     * @return timeslot info map
     */
    @ApiOperation("get time slot info by unit id and timeslot name")
    @RequestMapping(value = "/unit/{unitId}/time_slot_name", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> getTimeSlotByUnitIdAndTimeSlotName(@PathVariable long unitId, @RequestBody Long timeSlotExternalId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getTimeSlotByUnitIdAndTimeSlotExternalId(unitId, timeSlotExternalId));
    }


    /**
     * @param unitId
     * @return
     */
    @ApiOperation("update one_time sync settings of unit")
    @RequestMapping(value = "/unit/{unitId}/one_time_sync", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateOnetimeSyncSettings(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.updateOneTimeSyncsettings(unitId));
    }

    /**
     * @param unitId
     * @return
     */
    @ApiOperation("update one_time sync settings of unit")
    @RequestMapping(value = "/unit/{unitId}/auto_generate_task_settings", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateAutoGenerateTaskSettings(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.updateAutoGenerateTaskSettings(unitId));
    }

    /*
     * This endpoint in called from task micro service to get TaskDemand Supplier Info by unit id
     * @param unitId
     * @return organization info map
     */
    @ApiOperation("get TaskDemand Supplier  info by unit id ")
    @RequestMapping(value = "/unit/{unitId}/getTaskDemandSupplierInfo", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getTaskDemandSupplierInfo(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getTaskDemandSupplierInfo(unitId));
    }

    /*
     * This endpoint in called from task micro service to get Parent Organization Of CityLevel unit id
     * @param unitId
     * @return OrganizationDTO
     */
    @ApiOperation("get ParentOrganizationOfCityLevel by unit id ")
    @RequestMapping(value = "/unit/{unitId}/getParentOrganizationOfCityLevel", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getParentOrganizationOfCityLevel(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getParentOrganizationOfCityLevel(unitId));
    }

    /*
     * This endpoint in called from task micro service to get Parent Organization Of Unit unit id
     * @param unitId
     * @return OrganizationDTO
     */
    @ApiOperation("get ParentOfOrganization by unit id ")
    @RequestMapping(value = "/unit/{unitId}/getParentOfOrganization", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getParentOfOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getParentOfOrganization(unitId));
    }

    /*
     * This endpoint in called from task micro service to get Organization By TeamId
     * @param teamId
     * @return OrganizationDTO
     */
    @ApiOperation("get getOrganizationTypeHierarchy By TeamId ")
    @RequestMapping(value = "/getOrganizationByTeamId/{teamId}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getOrganizationByTeamId(@PathVariable long teamId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getOrganizationByTeamId(teamId));
    }

    /*
     * This endpoint in called from task micro service to get TimeSlot By UnitId and TimeSlotId
     * @param teamId
     * @return OrganizationDTO
     */
    @ApiOperation(value = "Get time slot")
    @RequestMapping(value = "/unit/{unitId}/time_slot/{timeSlotId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getTimeSlotByUnitIdAndTimeSlotId(@PathVariable long unitId, @PathVariable long timeSlotId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getTimeSlotByUnitIdAndTimeSlotId(unitId, timeSlotId));
    }

    /*
     * This endpoint in called from task micro service to get TaskDemand Supplier Info by unit id
     * @param unitId
     * @return organization info map
     */
    @ApiOperation("get organization by external id ")
    @RequestMapping(value = "/external/{externalId}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getOrganizationByExternalId(@PathVariable String externalId,
                                                                    @RequestParam("staffTimeCareId") Long staffExternalId,
                                                                    @RequestParam("staffTimeCareEmploymentId") Long staffTimeCareEmploymentId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getOrganizationAndStaffByExternalId(externalId,staffExternalId,staffTimeCareEmploymentId));
    }

    @RequestMapping(value = "/timecare_task/prerequisites", method = RequestMethod.POST)
    @ApiOperation("get required data for creation of time care task")
    public ResponseEntity<Map<String, Object>> getPrerequisitesForTimeCareTask(@RequestBody GetWorkShiftsFromWorkPlaceByIdResult workShift) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getPrerequisitesForTimeCareTask(workShift));
    }

    @RequestMapping(value = "/verifyOrganizationExpertise", method = RequestMethod.POST)
    @ApiOperation("verify organization skill and  and expertize are in DB")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>>
    verifyOrganizationExpertise(@RequestBody OrganizationMappingActivityTypeDTO organizationMappingActivityTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.verifyOrganizationExpertise(organizationMappingActivityTypeDTO));
    }

    /**
     * @return List of Organization- All organization in db.
     */
    @ApiOperation(value = "Get all Organization Ids")
    @RequestMapping(value = "/ids", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllOrganizationIds() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getAllOrganizationIds());
    }

    @RequestMapping(value = "/country_admins_ids/{countryAdminsOfUnitId}", method = RequestMethod.GET)
    @ApiOperation("get unit manager")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCountryAdminsIds(@PathVariable long countryAdminsOfUnitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffService.getCountryAdminIds(countryAdminsOfUnitId));
    }

    @RequestMapping(value = "/unit_manager_ids/{unitManagerOfUnitId}", method = RequestMethod.GET)
    @ApiOperation("get unit manager")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnitManagerIds(@PathVariable long unitManagerOfUnitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffService.getUnitManagerIds(unitManagerOfUnitId));
    }

    @RequestMapping(value = UNIT_URL + "/organizationTypeAndSubTypes", method = RequestMethod.GET)
    @ApiOperation("get All organization types and  and Sub org by unitId")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationTypeAndSubTypes(@RequestParam("type") String type, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getOrganizationTypeAndSubTypes(unitId, type));
    }

    @RequestMapping(value = "/unit/{unitId}/saveKMDExternal", method = RequestMethod.POST)
    @ApiOperation("Save KMD External of unitId")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveKMDExternalId(@PathVariable long unitId, @RequestBody OrganizationExternalIdsDTO organizationExternalIdsDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.saveKMDExternalId(unitId, organizationExternalIdsDTO));
    }

    @RequestMapping(value = "/unit/{unitId}/saveTimeSlotDeduction", method = RequestMethod.POST)
    @ApiOperation("Save KMD External of unitId")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveTimeSlotDeduction(@PathVariable long unitId, @RequestBody TimeSlotsDeductionDTO timeSlotsDeductionDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.saveTimeSlotPercentageDeduction(unitId, timeSlotsDeductionDTO));
    }


    @RequestMapping(value = "/unit/{unitId}/saveKMDExternal", method = RequestMethod.GET)
    @ApiOperation("Save KMD External of unitId")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getKMDExternalId(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getKMDExternalId(unitId));
    }

    @RequestMapping(value = "/unit/{unitId}/saveTimeSlotDeduction", method = RequestMethod.GET)
    @ApiOperation("Save KMD External of unitId")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTimeSlotDeduction(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getTimeSlotPercentageDeduction(unitId));
    }

    /**
     * @param unitId
     * @return
     * @auther anil maurya
     * use this endpoint from activity micro service via rest client
     */
    @ApiOperation(value = "Get skills and organizationTypes of organization")
    @RequestMapping(value = "/unit/{unitId}/skill/orgTypes", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationAvailableSkillsAndOrganizationTypesSubTypes(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getOrganizationAvailableSkillsAndOrganizationTypesSubTypes(unitId));
    }

    @RequestMapping(value = "/vehicleList", method = RequestMethod.GET)
    @ApiOperation("Get Vehicle list of unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getVehicleList(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getVehicleList(organizationId));

    }

    @ApiOperation(value = "Get Organization Clients with filters")
    @RequestMapping(value = "/unit/{unitId}/client/filters", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationClientsWithFilters(@PathVariable Long unitId, @RequestBody ClientFilterDTO clientFilterDTO,
                                                                                 @RequestParam("start") String start, @RequestParam("moduleId") String moduleId,@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientService.getOrganizationClientsWithFilter(unitId, clientFilterDTO, start, moduleId,organizationId));
    }

    @RequestMapping(value = UNIT_URL + "/dayTypebydate", method = RequestMethod.GET)
    @ApiOperation("get dayType in country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDayType(@PathVariable Long organizationId, @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getDayType(organizationId, date));

    }

    @RequestMapping(value = "/addStaffFavouriteFilters", method = RequestMethod.POST)
    @ApiOperation("verify staff has unit employment in unit or not ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addStaffFavouriteFilters(@RequestBody StaffFilterDTO staffFilterDTO,@PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.addStaffFavouriteFilters(staffFilterDTO,organizationId));
    }


    @RequestMapping(value = "/updateStaffFavouriteFilters", method = RequestMethod.POST)
    @ApiOperation("verify staff has unit employment in unit or not ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateStaffFavouriteFilters(@PathVariable long organizationId, @RequestBody StaffFilterDTO staffFilterDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.updateStaffFavouriteFilters(staffFilterDTO,organizationId));
    }

    @RequestMapping(value = "/removeStaffFavouriteFilters/{staffFavouriteFilterId}", method = RequestMethod.DELETE)
    @ApiOperation("verify staff has unit employment in unit or not ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> removeStaffFavouriteFilters(@PathVariable Long staffFavouriteFilterId,@PathVariable long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.removeStaffFavouriteFilters(staffFavouriteFilterId,organizationId));
    }

    @RequestMapping(value = "/getStaffFavouriteFilters/{moduleId}", method = RequestMethod.GET)
    @ApiOperation("verify staff has unit employment in unit or not ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffFavouriteFilters(@PathVariable long organizationId, @PathVariable String moduleId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.getStaffFavouriteFilters(moduleId, organizationId));
    }

    // DayType
    @ApiOperation(value = "Get DayType by unitID")
    @RequestMapping(value = UNIT_URL + "/dayType", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDayTypeByOrganization(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getAllDayTypeofOrganization(organizationId));
    }

    @RequestMapping(value = UNIT_URL + "/skill_category", method = RequestMethod.GET)
    @ApiOperation("Get a skillCategory by id")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllSkillCategory(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillCategoryService.findSkillCategoryByUnitId(unitId));
    }

    @ApiOperation(value = "Get DayType by unitID")
    @RequestMapping(value = "/units", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnitsByOrganizationID(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getUnitsByOrganizationIs(organizationId));
    }

    @ApiOperation(value = "Add custom name for Organization Service")
    @RequestMapping(value = "/unit/{unitId}/organization_service/{serviceId}", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updateCustomNameOfService(@PathVariable Long unitId, @PathVariable Long serviceId, @RequestBody OrganizationServiceDTO organizationServiceDTO,
                                                                         @RequestParam("type") String type) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServiceService.updateCustomNameOfService(serviceId, unitId, organizationServiceDTO.getCustomName(), type));
    }

    @ApiOperation(value = "Add custom name for Organization Sub Service")
    @RequestMapping(value = "/unit/{unitId}/organization_sub_service/{serviceId}", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updateCustomNameOfSubService(@PathVariable Long unitId, @PathVariable Long serviceId, @RequestBody OrganizationServiceDTO organizationServiceDTO,
                                                                            @RequestParam("type") String type) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationServiceService.updateCustomNameOfSubService(serviceId, unitId, organizationServiceDTO.getCustomName(), type));
    }

    /* Not in use
    @ApiOperation(value = "Get timetype_presencetype by unitID")
    @RequestMapping(value = UNIT_URL + "/timetype_presencetype", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllPresenceTypeAndTimeTypesByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, presenceTypeService.getAllPresenceTypeAndTimeTypesByUnitId(unitId));
    }
    */

    @ApiOperation(value = "Get available time zones")
    @RequestMapping(value = UNIT_URL + "/timeZones", method = RequestMethod.GET)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllTimeZones(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getAvailableZoneIds(unitId));
    }

    @ApiOperation(value = "Assign time zone to unit")
    @RequestMapping(value = UNIT_URL + "/timeZone", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> assignUnitTimeZone(@PathVariable Long unitId, @RequestBody Map<String, Object> data) {
        String zoneId = (String) data.get("zoneId");
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.assignUnitTimeZone(unitId, zoneId));
    }

    @ApiOperation(value = "Assign Default Opening Hours to Unit")
    @RequestMapping(value = UNIT_URL + "/setDefaultOpeningHours", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> setDefaultOpeningHours(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openningHourService.setDefaultOpeningHours(unitId));
    }

    @RequestMapping(value = UNIT_URL + "/cta/default-data", method = RequestMethod.GET)
    @ApiOperation("get default data for cta_response rule template")
    public ResponseEntity<Map<String, Object>> getDefaultDataForCTARuleTemplate(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryService.getDefaultDataForCTATemplate(null, unitId));
    }

    /*
        * By Vipul
        * API to get expertise employment Type o for activity  MASTER DATA
        */
    @RequestMapping(value = UNIT_URL + "/activity-mapping-details", method = RequestMethod.GET)
    @ApiOperation("get  expertise  employment Type  for organization")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getEmploymentTypeWithExpertise(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getEmploymentTypeWithExpertise(unitId));
    }

    @RequestMapping(value ="/WTARelatedInfo", method = RequestMethod.GET)
    @ApiOperation("get  Wta related info")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getWTARelatedInfo(@RequestParam Long countryId,@RequestParam Long organizationId,@RequestParam Long organizationSubTypeId,@RequestParam Long organizationTypeId,@RequestParam Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getWTARelatedInfo(countryId, organizationId, organizationSubTypeId, organizationTypeId, expertiseId));
    }

    @RequestMapping(value = UNIT_URL + "/time_zone", method = RequestMethod.GET)
    //@ApiOperation("Get Time Zone Organization")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTimeZoneOfUnit(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getTimeZoneStringOfUnit(unitId));
    }
//    @Pavan
//    Add Apis for Time slots for Shift planning

    @ApiOperation(value = "Get Organization Time Slot sets")
    @RequestMapping(value = "/unit/{unitId}/shift_planning/time_slot_set", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getShiftPlanningTimeSlotSetsByUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getShiftPlanningTimeSlotSetsByUnit(unitId));
    }

    @ApiOperation(value = "Get Organization Time Slots")
    @RequestMapping(value = "/unit/{unitId}/time_slot_set/{timeSlotSetId}/shift_planning/time_slot", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getShiftPlanningTimeSlotsByUnit(@PathVariable Long timeSlotSetId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getShiftPlanningTimeSlotsById(timeSlotSetId));
    }

    @ApiOperation(value = "Get Default data for Orders")
    @RequestMapping(value = UNIT_URL+"/order/default_data", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDefaultDataForOrder(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getDefaultDataForOrder(unitId));
    }

    @ApiOperation(value = "Init optplanner integration")
    @RequestMapping(value = "/unit/{unitId}/planner_integration", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> initialOptaplannerSync(@PathVariable Long organizationId,@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.initialOptaplannerSync(organizationId,unitId));

    }

    @ApiOperation(value = "Get DayType and Presence Type")
    @RequestMapping(value = "/unit/{unitId}/getWtaTemplateDefaultDataInfoByUnitId", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getWtaTemplateDefaultDataInfo(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getWtaTemplateDefaultDataInfoByUnitId(unitId));

    }


    @RequestMapping(value = UNIT_URL + "/unit_position/expertise", method = RequestMethod.GET)
    @ApiOperation("fetch Map of unit position id and expertise id")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>>  getExpertiseOfUnitPosition(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.getUnitPositionExpertiseMap(unitId));
    }


    @ApiOperation(value = "Get Default data for Rule Template")
    @RequestMapping(value = "/country/{countryId}/rule_template/default_data", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDefaultDataForRuleTemplate(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getDefaultDataForRuleTemplate(countryId));
    }

    @ApiOperation(value = "Get Default data for Rule Template based on UnitId")
    @RequestMapping(value = "/unit/{unitId}/rule_template/default_data", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getDefaultDataForRuleTemplateByUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getDefaultDataForRuleTemplateByUnit(unitId));
    }

    @ApiOperation(value = "Update Unit settings")
    @PutMapping(value = "/unit/{unitId}/updateOrganizationSettings")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOrganizationSettings(@PathVariable Long unitId,@RequestBody OrganizationSettingDTO organizationSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.updateOrganizationSettings(organizationSettingDTO,unitId));
    }

    @ApiOperation(value = "get Unit settings")
    @GetMapping(value = "/unit/{unitId}/getOrganizationSettings")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationSettings(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getOrganizationSettings(unitId));
    }

    @ApiOperation(value = "get Unit and Parent Organization and Country Id")
    @GetMapping(value = "/unit/parent_org_and_country")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getParentOrganizationAndCountryIdsOfUnit() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getParentOrganizationAndCountryIdsOfUnit());
    }

    @ApiOperation(value = "Create Prefered Time window")
    @PostMapping(value = "/unit/{unitId}/prefer_time_window")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createPreferedTimeWindow(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                vrpClientService.createPreferedTimeWindow(unitId));
    }

    @ApiOperation(value = "get Prefered Time window")
    @GetMapping(value = "/unit/{unitId}/prefer_time_window")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPreferedTimeWindow(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                vrpClientService.getPreferedTimeWindow(unitId));
    }


    @ApiOperation(value = "get Cta basic info")
    @GetMapping(value = COUNTRY_URL+"/cta_basic_info")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getCTABasicDetailInfo(@PathVariable Long countryId,@RequestParam(required = false) Long expertiseId,@RequestParam(required = false) Long organizationSubTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getCTABasicDetailInfo(expertiseId,organizationSubTypeId,countryId));
    }

    @ApiOperation(value = "get organization ids by orgSubType ids")
    @PostMapping(value = "/orgtype/get_organization_ids")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationIdsBySubOrgTypeId(@RequestBody List<Long> orgTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getOrganizationIdsBySubOrgTypeId(orgTypeId));
    }
}
