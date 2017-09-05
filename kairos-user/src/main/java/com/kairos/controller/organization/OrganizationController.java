package com.kairos.controller.organization;

import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.organization.team.TeamDTO;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.auth.UserAuthentication;
import com.kairos.persistence.model.user.client.ClientStaffDTO;
import com.kairos.persistence.model.user.department.Department;
import com.kairos.persistence.model.user.resources.Resource;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.tpa_services.IntegrationConfiguration;
import com.kairos.service.client.ClientBatchService;
import com.kairos.service.client.ClientService;
import com.kairos.service.language.LanguageService;
import com.kairos.service.organization.*;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.region.RegionService;
import com.kairos.service.resources.ResourceService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.staff.StaffService;
import com.kairos.service.tpa_services.IntegrationConfigurationService;
import com.kairos.util.response.ResponseHandler;
import com.kairos.util.timeCareShift.GetWorkShiftsFromWorkPlaceByIdResult;
import com.kairos.util.userContext.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.UNIT_URL;


/**
 * OrganizationController
 * 1.Calls Organization Service
 * 2. Call for CRUD operatio`n on Organization using OrganizationService.
 */
@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class OrganizationController {

    @Inject
    private OrganizationService organizationService;
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

    /**
     * Create new organization in db and return created organization
     *
     * @param organizationDTO
     * @return Organization
     */
    @ApiOperation(value = "Create a New Organization(Location)")
    @RequestMapping(value = UNIT_URL + "/unit", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addOrganization(@Validated @RequestBody OrganizationDTO organizationDTO, @PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.createNewUnit(organizationDTO, unitId));
    }


    /**
     *
     */
    @ApiOperation(value = "Get a Organization(Location)")
    @RequestMapping(value = UNIT_URL + "/unit", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getUnits(unitId));
    }

    /**
     *
     */
    @ApiOperation(value = "Get organization herirchy data")
    @RequestMapping(value = UNIT_URL + "/manage_hierarchy", method = RequestMethod.GET)
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
    @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
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
                skillService.addNewSkill(unitId, skillId, isSelected, type,visitourId));
    }

    @ApiOperation(value = "update visitour id of skill for an organization")
    @RequestMapping(value = "/unit/{unitId}/skill/{skillId}/visitour_details", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateVisitourIdOfSkill(@PathVariable long unitId, @PathVariable long skillId, @RequestParam("type") String type,@RequestBody Map<String, Object> data) {

        if (data.get("visitourId") != null && data.get("visitourId") != "") {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, skillService.updateVisitourIdOfSkill(unitId, skillId, (String) data.get("visitourId"),type));
        }
        throw new InternalError("Visitour id can not be null or empty");
    }

    @ApiOperation(value = "get skills of staff")
    @RequestMapping(value = "/unit/{unitId}/staff/skills", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffSkills(@RequestParam("type") String type,@PathVariable long unitId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillService.getStaffSkills(unitId,type));
    }


    @ApiOperation(value = "assign skill to staff")
    @RequestMapping(value = "/unit/{unitId}/skill/{skillId}/assign", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> assignSkillToStaff(@PathVariable long skillId, @RequestParam("type") String type,@PathVariable long unitId, @RequestBody Map<String, Object> data) {


        long staffId = Long.valueOf((String) data.get("staffId"));
        boolean isSelected = (boolean) data.get("isSelected");
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillService.assignSkillToStaff(unitId, staffId, skillId, isSelected,type));
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

    @ApiOperation(value = "create new time slot")
    @RequestMapping(value = "/unit/{unitId}/time_slot", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createTimeSlot(@PathVariable long unitId, @Validated @RequestBody TimeSlotDTO timeSlotDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.createTimeSlot(unitId, timeSlotDTO));
    }

    @ApiOperation(value = "update time slot type")
    @RequestMapping(value = "/unit/{unitId}/time_slot_type", method = RequestMethod.PUT)
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTimeSlotType(@PathVariable long unitId, @RequestBody Map<String, Object> timeSlotType) {
        boolean standardTimeSlot = (boolean) timeSlotType.get("standardTimeSlot");
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.updateTimeSlotType(unitId, standardTimeSlot));
    }


    @ApiOperation(value = "Update time slot")
    @RequestMapping(value = "/unit/{unitId}/time_slot", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateTimeSlot(@PathVariable long unitId, @Validated @RequestBody List<TimeSlotDTO> timeSlotDTO) {

        List<Map<String, Object>> response = timeSlotService.updateTimeSlot(unitId, timeSlotDTO);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, true, Collections.emptyMap());
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @ApiOperation(value = "Delete time slot")
    @RequestMapping(value = "/unit/{unitId}/time_slot/{timeSlotId}", method = RequestMethod.DELETE)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteTimeSlot(@PathVariable long unitId, @PathVariable long timeSlotId) {

        boolean timeSlotDeleted = timeSlotService.deleteTimeSlot(unitId, timeSlotId);
        if (timeSlotDeleted) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, false);
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
    public ResponseEntity<Map<String, Object>> getOrganizationClients(@PathVariable Long organizationId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                clientService.getOrganizationClients(unitId));
    }

    @ApiOperation(value = "Get Organization Clients with min details")
    @RequestMapping(value = "/unit/{unitId}/client/planning", method = RequestMethod.GET)
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
                clientService.getOrganizationAllClients(organizationId,unitId, userId));
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
    @ApiOperation("Get general details of Client")
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

    @RequestMapping(value = "/parent/{countryId}", method = RequestMethod.PUT)
    @ApiOperation("Update Parent Organization")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateParentOrganization(@PathVariable Long countryId, @PathVariable long organizationId, @RequestBody ParentOrganizationDTO data) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.updateParentOrganization(data, organizationId));
    }

    @RequestMapping(value = "/parent/{countryId}", method = RequestMethod.POST)
    @ApiOperation("Create Parent Organization")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createParentOrganization(@PathVariable Long countryId, @RequestBody ParentOrganizationDTO organization) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.createParentOrganization(organization, countryId));
    }

    @RequestMapping(value = "/parent/{countryId}", method = RequestMethod.GET)
    //@ApiOperation("Get Parent Organization")
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getParentOrganization(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getParentOrganization(countryId));
    }

    @RequestMapping(value = "/unit", method = RequestMethod.GET)
    @ApiOperation("get child units of parent organization")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getChildUnits(@PathVariable long organizationId, @RequestParam(value = "moduleId") String moduleId,
                                                             @RequestParam(value = "userId") long userId) {

        //TODO there is hardcoded module id,later will get from url @prabjot
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationHierarchyService.getChildUnits(organizationId, userId, moduleId));
    }

    @RequestMapping(value = "unit/{unitId}/resources", method = RequestMethod.GET)
    @ApiOperation("Get Organization Resource of a Unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationResources(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                resourceService.getUnitResources(unitId));
    }

    @RequestMapping(value = "unit/{unitId}/resources/type", method = RequestMethod.GET)
    @ApiOperation("Get Organization Resource Type Array")
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOrganizationResourcesTypes() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                resourceService.getUnitResourcesTypes());
    }

    @RequestMapping(value = "unit/{unitId}/resources", method = RequestMethod.PUT)
    @ApiOperation("Update Resource of a Unit")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateOrganizationResources(@PathVariable Long unitId, @RequestBody Resource resource) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                resourceService.setUnitResource(resource, unitId));
    }


    @RequestMapping(value = "unit/{unitId}/unit_manager", method = RequestMethod.POST)
    @ApiOperation("create unit manager")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createUnitManager(@PathVariable long unitId, @Validated @RequestBody UnitManagerDTO unitManagerDTO) {
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
    @RequestMapping(value = "/integration_service", method = RequestMethod.GET)
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
    @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
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
    @RequestMapping(value = "unit/{unitId}/mapImportedService/{imPortedServiceId}", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> mapImportedService(@PathVariable long imPortedServiceId, @RequestBody long serviceId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationServiceService.mapImportedService(imPortedServiceId, serviceId));
    }


    /**
     * @auther anil maurya
     * this endpoint is called from task micro service
     * @param unitId
     * @return
     */
    @ApiOperation("get assigned staff to citizen")
    @RequestMapping(value = "/unit/{unitId}/common_data", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getCommonDataOfOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getCommonDataOfOrganization(unitId));
    }


    /**
     * @auther anil maurya
     * this endpoint is called from task micro service
     * @param unitId
     * @return
     */
    @ApiOperation("get visitation info for a unit")
    @RequestMapping(value = "/unit/{unitId}/unit_visitation", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getUnitVisitationInfo(@PathVariable Long organizationId,@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getUnitVisitationInfo(organizationId,unitId));
    }

    /**
     * this url will be call from rest template,
     * it provides skills of organization for task type tab
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
     * @param unitId
     * @return
     */
    @ApiOperation(value = "Get skills of organization")
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
    ResponseEntity<Map<String, Object>> getTimeSlotByUnitIdAndTimeSlotName(@PathVariable long unitId, @RequestBody String timeSlotName) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeSlotService.getTimeSlotByUnitIdAndTimeSlotName(unitId, timeSlotName));
    }


    /**
     *
     * @param unitId
     * @return
     */
    @ApiOperation("update one_time sync settings of unit")
    @RequestMapping(value = "/unit/{unitId}/one_time_sync", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateOnetimeSyncSettings(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.updateOneTimeSyncsettings(unitId));
    }

    /**
     *
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
    @ApiOperation("get getOrganization By TeamId ")
    @RequestMapping(value = "/unit/{unitId}/getOrganizationByTeamId/{teamId}", method = RequestMethod.GET)
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
        return ResponseHandler.generateResponse(HttpStatus.OK, true,  timeSlotService.getTimeSlotByUnitIdAndTimeSlotId(unitId, timeSlotId));
    }

    /*
     * This endpoint in called from task micro service to get TaskDemand Supplier Info by unit id
     * @param unitId
     * @return organization info map
     */
    @ApiOperation("get organization by external id ")
    @RequestMapping(value = "/external/{externalId}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getOrganizationByExternalId(@PathVariable String externalId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationService.getOrganizationByExternalId(externalId));
    }

    @RequestMapping(value = "/timecare_task/prerequisites", method = RequestMethod.POST)
    @ApiOperation("get required data for creation of time care task")
    public ResponseEntity<Map<String,Object>> getPrerequisitesForTimeCareTask(@RequestBody GetWorkShiftsFromWorkPlaceByIdResult workShift) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                organizationService.getPrerequisitesForTimeCareTask(workShift));
    }

    /**
     * @return List of Organization- All organization in db.
     */
    @ApiOperation(value = "Get all Organization Ids")
    @RequestMapping(value = "/ids",method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public List<Map<String, Object>> getAllOrganizationIds() {
        return organizationService.getAllOrganization();
    }

}


