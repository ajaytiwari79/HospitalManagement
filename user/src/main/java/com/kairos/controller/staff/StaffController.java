package com.kairos.controller.staff;

import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.dto.user.user.password.PasswordUpdateByAdminDTO;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.staff.*;
import com.kairos.persistence.model.staff.employment.EmploymentUnitPositionDTO;
import com.kairos.persistence.model.staff.employment.StaffEmploymentDetail;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.country.EmploymentTypeService;
import com.kairos.service.skill.SkillService;
import com.kairos.service.staff.*;
import com.kairos.service.unit_position.UnitPositionJobService;
import com.kairos.dto.user.employment.EmploymentDTO;
import com.kairos.dto.user.staff.staff.StaffCreationDTO;
import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.utils.DateConverter;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.wrapper.staff.StaffEmploymentWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;
import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION;


/**
 * Created by prabjot on 24/10/16.
 */
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL + "/staff")
@Api(value = API_ORGANIZATION_UNIT_URL + "/staff")
public class StaffController {

    @Inject
    private StaffService staffService;
    @Inject
    private AccessGroupService accessGroupService;
    @Inject
    private PositionService positionService;
    @Inject
    private ApiExternalStaffService apiExternalStaffService;
    @Inject
    private SkillService skillService;
    @Inject
    private EmploymentTypeService employmentTypeService;
    @Inject
    private StaffRetrievalService staffRetrievalService;
    @Inject
    private UnitPositionJobService unitPositionJobService;


    @RequestMapping(value = "/{staffId}/employment_details", method = RequestMethod.PUT)
    @ApiOperation("update staff employment details")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveEmploymentInfo(@PathVariable long unitId, @PathVariable long staffId, @Validated @RequestBody StaffEmploymentDetail staffEmploymentDetail) {
        Map<String, Object> response = positionService.saveEmploymentDetail(unitId, staffId, staffEmploymentDetail);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        } else {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
        }
    }

    @RequestMapping(value = "/{staffId}/photo", method = RequestMethod.POST)
    @ApiOperation("upload portrait")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> uploadPhoto(@PathVariable long staffId, @RequestParam("file") MultipartFile file) {
        if (file != null && file.getSize() == 0) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        String fileName = staffService.uploadPhoto(staffId, file);
        if (fileName == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, fileName);
    }

    @RequestMapping(value = "/{staffId}/photo", method = RequestMethod.DELETE)
    @ApiOperation("delete portrait")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> removePhoto(@PathVariable long staffId) {
        if (staffService.removePhoto(staffId)) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);
    }

    @RequestMapping(value = "/{staffId}/personal_info", method = RequestMethod.PUT)
    @ApiOperation("update staff personal information")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> savePersonalDetail(@PathVariable long unitId, @PathVariable long staffId, @Validated @RequestBody StaffPersonalDetail staffPersonalDetail) throws ParseException {
        StaffPersonalDetail response = staffService.savePersonalDetail(staffId, staffPersonalDetail, unitId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/{staffId}/personal_info", method = RequestMethod.GET)
    @ApiOperation("get personal information of staff")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPersonalInfo(@PathVariable long unitId, @PathVariable long staffId, @RequestParam("type") String type) {
        Map<String, Object> personalInfo = staffRetrievalService.getPersonalInfo(staffId, unitId, type);
        if (personalInfo == null) {
            return null;
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, personalInfo);
    }

    @RequestMapping(value = "/{staffId}/employment", method = RequestMethod.POST)
    @ApiOperation("update employments of staff")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createEmployment(@PathVariable long staffId, @RequestBody Map<String, Object> employmentDetail) {

        long accessGroupId = Long.parseLong((String) employmentDetail.get("roleId"));
        boolean isCreated = (boolean) employmentDetail.get("isCreated");
        long unitId = Long.parseLong((String) employmentDetail.get("organizationId"));
        Map<String, Object> response = positionService.createUnitPermission(unitId, staffId, accessGroupId, isCreated);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);

    }

    @RequestMapping(value = "/{staffId}/unit_employments", method = RequestMethod.GET)
    @ApiOperation("get employments of staff")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getEmployments(@PathVariable long staffId, @PathVariable long unitId, @RequestParam("type") String type) {
        Map<String, Object> responseData = new HashMap<String, Object>(2);
        responseData.put("employments", positionService.getEmployments(staffId, unitId, type));
        responseData.put("employmentTypes", employmentTypeService.getEmploymentTypeOfOrganization(unitId, false));
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responseData);
    }

    @RequestMapping(value = "/{staffId}/partial_leave", method = RequestMethod.POST)
    @ApiOperation("update employments of staff")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addPartialLeave(@PathVariable long staffId, @PathVariable long unitId,
                                                               @Validated @RequestBody PartialLeaveDTO partialLeaveDTO, @RequestParam("type") String type) throws ParseException {
        Map<String, Object> updatedObj = positionService.addPartialLeave(staffId, unitId, type, partialLeaveDTO);
        if (updatedObj == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, updatedObj);
    }

    @RequestMapping(value = "/{staffId}/partial_leave", method = RequestMethod.GET)
    @ApiOperation("get partial leaves of staff")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPartialLeaves(@PathVariable long staffId, @PathVariable long unitId, @RequestParam("type") String type) throws ParseException {
        Map<String, Object> response = positionService.getPartialLeaves(staffId, unitId, type);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @RequestMapping(value = "/{staffId}/workplaces", method = RequestMethod.GET)
    @ApiOperation("get workplaces of staff")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getWorkPlace(@PathVariable long staffId, @PathVariable long unitId, @RequestParam("type") String type) {
        List<Map<String, Object>> workPlaces = positionService.getWorkPlaces(staffId, unitId, type);
        if (workPlaces == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, workPlaces);
    }

    @RequestMapping(value = "/{staffId}/workplaces", method = RequestMethod.POST)
    @ApiOperation("update workplaces of staff")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> editWorkPlace(@PathVariable long staffId, @RequestBody Map<String, Object> data) {
        List<Long> teamIds = (List<Long>) data.get("teamIds");
        Staff staff = positionService.editWorkPlace(staffId, teamIds);
        if (staff == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staff);
    }

    @RequestMapping(value = "/{staffId}/note", method = RequestMethod.POST)
    @ApiOperation("save notes")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> saveNote(@PathVariable long staffId, @RequestBody Map<String, String> reqData) {
        Map<String, Object> response = staffService.saveNotes(staffId, reqData.get("generalNote"), reqData.get("reqFromPerson"));

        if (response != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
    }

    @RequestMapping(value = "/{staffId}/note", method = RequestMethod.GET)
    @ApiOperation("get notes")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getNotes(@PathVariable long staffId) {
        Map<String, Object> response = staffRetrievalService.getNotes(staffId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);

    }

    @RequestMapping(value = "/{staffId}/note", method = RequestMethod.DELETE)
    @ApiOperation("delete notes")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteNotes(@PathVariable long staffId) {
        Map<String, Object> response = staffService.deleteNote(staffId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);

    }

    /**
     * Get All Organization Staff by it's type
     *
     * @param type
     * @param id
     * @return
     * @Modify vipul
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation("get staff")
    public ResponseEntity<Map<String, Object>> getStaff(@RequestParam String type, @RequestParam long id, @RequestParam("unitPosition") boolean allStaffRequired) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffRetrievalService.getStaff(type, id, allStaffRequired));
    }

    @RequestMapping(value = "/filter", method = RequestMethod.POST)
    @ApiOperation("get staff")
    public ResponseEntity<Map<String, Object>> getStaffWithFilters(@RequestBody StaffFilterDTO staffFilterDTO, @PathVariable Long unitId, @RequestParam String type, @RequestParam long id, @RequestParam String moduleId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffRetrievalService.getStaffWithFilter(unitId, type, id, staffFilterDTO, moduleId));
    }

    /**
     * unit manager can assign specific expertise to staff
     * every staff will have one expertise at time
     *
     * @param staffId
     * @param expertiseIds
     * @return
     */
    @RequestMapping(value = "/{staffId}/expertise/{expertiseId}", method = RequestMethod.POST)
    @ApiOperation("assign expertise to staff")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> assignExpertiseToStaff(@PathVariable long staffId, @RequestBody List<Long> expertiseIds) {
        Staff staff = staffService.assignExpertiseToStaff(staffId, expertiseIds);
        if (staff == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staff);
    }

    @RequestMapping(value = "/{staffId}/expertise/{expertiseId}", method = RequestMethod.GET)
    @ApiOperation("get expertise to staff")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertiseOfStaff(@PathVariable long countryId, @PathVariable long staffId) {
        Map<String, Object> response = staffRetrievalService.getExpertiseOfStaff(countryId, staffId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, response);
    }

    // Skills
    @ApiOperation(value = "assign Skills to staff")
    @RequestMapping(value = "/{staffId}/skill", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> assignSkillsToStaff(@PathVariable long unitId, @RequestBody StaffSkillDTO staffSkillDTO, @PathVariable long staffId) {

        Object response = skillService.assignSkillToStaff(staffId, staffSkillDTO.getRemovedSkillId(), staffSkillDTO.isSelected(), unitId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, response);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);
    }

    @ApiOperation(value = "Get skills of staff")
    @RequestMapping(value = "/{staffId}/skill", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getSkills(@PathVariable long unitId, @PathVariable long staffId, @RequestParam("type") String type) {
        Map<String, Object> skills = skillService.getSkills(staffId, unitId, type);
        if (skills == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skills);
    }

    @ApiOperation(value = "Update skill of staff")
    @RequestMapping(value = "/{staffId}/skill/{skillId}", method = RequestMethod.PUT)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateStaffSkillLevel(@PathVariable long unitId, @PathVariable long staffId, @PathVariable long skillId,
                                                                     @RequestBody Map<String, Object> skillInfo) throws ParseException {
        Skill.SkillLevel level = Skill.SkillLevel.valueOf((String) skillInfo.get("level"));
        long startDate = DateConverter.parseDate((String) skillInfo.get("startDate")).getTime();
        long endDate = DateConverter.parseDate((String) skillInfo.get("endDate")).getTime();
        boolean status = (boolean) skillInfo.get("status");
        skillService.updateStaffSkillLevel(staffId, skillId, level, startDate, endDate, status, unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, skillInfo);
    }

    // Working As tab
    @ApiOperation(value = "Get Planners and their location")
    @RequestMapping(value = "/type/planner/{organizationId}", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPlanners(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffRetrievalService.getPlannerInOrganization(organizationId));
    }

    @ApiOperation(value = "Get Manager and their location")
    @RequestMapping(value = "/type/manager/{organizationId}", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getManagers(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffRetrievalService.getManagersInOrganization(organizationId));
    }

    @ApiOperation(value = "Get Visitators and their location")
    @RequestMapping(value = "/type/visitator/{organizationId}", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getVisitators(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffRetrievalService.getVisitatorsInOrganization(organizationId));
    }

    @ApiOperation(value = "Get TeamLeaders and their location")
    @RequestMapping(value = "/type/team_leader/{organizationId}", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTeamLeaders(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffRetrievalService.getTeamLeadersInOrganization(organizationId));
    }


    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ApiOperation("Upload XLSX file ")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> batchCreateStaff(@PathVariable long unitId,
                                                                @RequestParam("file") MultipartFile multipartFile,
                                                                @RequestParam("accessGroupId") Long accessGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffService.batchAddStaffToDatabase(unitId, multipartFile, accessGroupId));
    }


    @RequestMapping(value = "/{staffId}/access_permissions", method = RequestMethod.GET)
    @ApiOperation("Get uploaded Staff as per orgnaizationID ")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAccessPermissions(@PathVariable long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                accessGroupService.getAccessPermissions(staffId));
    }


    @RequestMapping(value = "/timecare", method = RequestMethod.POST)
    @ApiOperation("Create Timecare Staff")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addStaffFromTimeCare(@RequestBody Map<String, Object> data) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                apiExternalStaffService.createTimeCareStaff(data));
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ApiOperation("Get All  Staff")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllStaff() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                staffService.getAllStaff());
    }

    @RequestMapping(value = "/{staffId}/deleteStaff/{positionId}", method = RequestMethod.DELETE)
    @ApiOperation("Permanent Delete staff node, don't invoke this method")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteStaffById(@PathVariable Long staffId, @PathVariable Long positionId) {
        Boolean status = staffService.deleteStaffById(staffId, positionId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                status);

    }

    @RequestMapping(value = "/country_admin", method = RequestMethod.POST)
    @ApiOperation("create country admin")
    public ResponseEntity<Map<String, Object>> createCountryAdmin(@RequestBody User user) {
        User admin = staffService.createCountryAdmin(user);
        if (admin == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false,
                    null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                admin);

    }

    @RequestMapping(value = "/{staffId}/permission", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> setPagePermissionToUser(@PathVariable long staffId, @RequestBody Map<String, Object> permission) {

        boolean read = (boolean) permission.get("read");
        boolean write = (boolean) permission.get("write");
        long accessGroupId = Long.parseLong((String) permission.get("accessGroupId"));
        long tabId = Long.parseLong((String) permission.get("tabId"));
        long unitId = Long.parseLong((String) permission.get("unitId"));
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.setPagePermissionToUser(staffId, unitId, accessGroupId, tabId, read, write));
    }

    @RequestMapping(value = "/{staffId}/external_id", method = RequestMethod.POST)
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateExternalId(@PathVariable long staffId, @RequestBody Map<String, Object> data) throws ParseException {
        long externalId = Long.parseLong((String) data.get("externalId"));
        apiExternalStaffService.updateExternalId(staffId, externalId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @RequestMapping(value = "/create_staff_from_web", method = RequestMethod.POST)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createStaffFromWeb(@PathVariable Long unitId, @Validated @RequestBody StaffCreationDTO staffCreationDTO) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.createStaffFromWeb(unitId, staffCreationDTO));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/basic_info")
    @ApiOperation("update staff from excel sheet")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    private ResponseEntity<Map<String, Object>> updateStaffFromExcel(@RequestParam("file") MultipartFile multipartFile) {
        staffService.updateStaffFromExcel(multipartFile);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }


    @RequestMapping(value = "/{staffId}/assigned_tasks", method = RequestMethod.GET)
    @ApiOperation("Get All Task types of a Staff")
    public ResponseEntity<Map<String, Object>> getAssignedTasksOfStaff(@PathVariable long unitId, @PathVariable long staffId, @RequestParam("date") String date) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService
                .getAssignedTasksOfStaff(unitId, staffId, date));
    }

    /**
     * @param unitId
     * @param staffDTO
     * @return
     */
    @RequestMapping(value = "/createStaff", method = RequestMethod.POST)
    @ApiOperation("createStaff")
    public ResponseEntity<Map<String, Object>> createStaff(@PathVariable long unitId, @RequestBody StaffDTO staffDTO) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.createStaffFromPlanningWorkflow(staffDTO, unitId));
    }


    /**
     * @param unitId
     * @param staffIds
     * @return
     * @auther anil maurya
     * this endpoint is called from task micro service
     */
    @RequestMapping(value = "/getsfAndsfSkill", method = RequestMethod.POST)
    @ApiOperation("getTeamStaffAndStaffSkill")
    public ResponseEntity<Map<String, Object>> getTeamStaffAndStaffSkill(@PathVariable Long unitId, @RequestBody List<Long> staffIds) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.getTeamStaffAndStaffSkill(unitId, staffIds));
    }

    /**
     * @return
     * @auther anil maurya
     * this endpoint is called from task micro service
     */

    @RequestMapping(value = "/getStaffInfo", method = RequestMethod.GET)
    @ApiOperation("Get loggedin Staff Info")
    public ResponseEntity<Map<String, Object>> getStaffInfo(OAuth2Authentication user) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.getStaffInfo(user.getUserAuthentication().getPrincipal().toString()));
    }


    /*
     * @Author Vipul
     * */

    @RequestMapping(value = "/staff_list", method = RequestMethod.GET)
    @ApiOperation("Get All staff List available in Org")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllStaffByUnitId(@PathVariable long unitId, @RequestParam("unitPosition") boolean allStaffRequired) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.getAllStaffByUnitId(unitId, allStaffRequired));
    }


    @RequestMapping(value = "/{staffId}/personal_details", method = RequestMethod.GET)
    @ApiOperation("get only personal details of staff by StaffId ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffInfoById(@PathVariable long unitId, @PathVariable long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.getStaffInfoById(staffId, unitId));
    }

    @RequestMapping(value = "/{staffId}", method = RequestMethod.GET)
    @ApiOperation("get Staff by StaffId ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffById(@PathVariable long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.getStaffById(staffId));
    }

    @RequestMapping(value = "/{staffId}/verifyUnitEmployment/{unitPositionId}", method = RequestMethod.GET)
    @ApiOperation("verify staff has unit employment in unit or not ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffEmploymentData(@RequestParam("type") String type, @RequestParam("shiftDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate shiftDate, @RequestParam(value = "reasonCodeIds", required = false) Set<Long> reasonCodeIds, @PathVariable long unitId, @PathVariable long staffId,
                                                                      @PathVariable Long unitPositionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffRetrievalService.getStaffEmploymentDataByUnitPositionIdAndStaffId(shiftDate, staffId, unitPositionId, unitId, type, reasonCodeIds));
    }

    // We need only limited data so we are making a substitute of above API
    @RequestMapping(value = "/{staffId}/unit_position/{unitPositionId}/functions", method = RequestMethod.GET)
    @ApiOperation("API for check unit position of staff and available functions and reasoncodes on unit")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffEmploymentData(@RequestParam("shiftDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate shiftDate,
                                                                      @PathVariable Long unitPositionId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffRetrievalService.getStaffEmploymentData(shiftDate, unitPositionId, unitId));
    }

    @RequestMapping(value = "/verifyUnitEmployments", method = RequestMethod.GET)
    @ApiOperation("verify staff has unit employment in unit or not ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffEmploymentsData(@PathVariable long unitId, @RequestParam("staffIds") List<Long> staffIds, @RequestParam("unitPositionIds") List<Long> unitPositionIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffRetrievalService.getStaffsEmploymentData(staffIds, unitPositionIds, unitId, ORGANIZATION));
    }

    @RequestMapping(value = "/{staffId}/verifyUnitEmployment", method = RequestMethod.GET)
    @ApiOperation("verify staff has unit employment in unit or not and get current unit position ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnitPositionOfStaff(@PathVariable long unitId, @PathVariable long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.getUnitPositionOfStaff(staffId, unitId));
    }


    @RequestMapping(value = "/current_user/{userId}", method = RequestMethod.GET)
    @ApiOperation("fetch staff from given userId ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffByUser(@PathVariable long userId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.getStaffByUserId(userId));
    }

    @RequestMapping(value = "/getStaffByExperties", method = RequestMethod.POST)
    @ApiOperation("getStaffByExperties")
    public ResponseEntity<Map<String, Object>> getStaffByExperties(@PathVariable Long unitId, @RequestBody List<Long> expertiesIds) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffRetrievalService.getStaffByExperties(unitId, expertiesIds));
    }

    @RequestMapping(value = "/{staffId}/employment", method = RequestMethod.PUT)
    @ApiOperation("update employment of staff")
    public ResponseEntity<Map<String, Object>> updateEmployment(@PathVariable Long unitId, @PathVariable long staffId, @RequestBody EmploymentDTO employmentDTO) throws Exception {

        String employmentEndDate = employmentDTO.getEndDate();//(String)employmentDetail.get("endDate");
        Long reasonCodeId = employmentDTO.getReasonCodeId();
        Long accessGroupId = employmentDTO.getAccessGroupIdOnEmploymentEnd();
        EmploymentUnitPositionDTO response = unitPositionJobService.updateUnitPositionEndDateFromEmployment(staffId, employmentEndDate, unitId, reasonCodeId, accessGroupId);
        if (response == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, Collections.EMPTY_MAP);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, response);

    }

    @RequestMapping(value = "/unitwise", method = RequestMethod.GET)
    @ApiOperation("fetch unit wise staff list ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnitWiseStaff() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffRetrievalService.getUnitWiseStaffList());
    }

    @RequestMapping(value = "/priority_group", method = RequestMethod.POST)
    @ApiOperation("get Staff by StaffId ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffByPriorityGroup(@PathVariable long unitId, @RequestBody StaffIncludeFilterDTO staffIncludeFilterDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffRetrievalService.getStaffByStaffIncludeFilterForPriorityGroups(staffIncludeFilterDTO, unitId));
    }

    @RequestMapping(value = "/emails", method = RequestMethod.POST)
    @ApiOperation("get email addresses of staffs")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getEmailsOfStaffByStaffIds(@RequestBody List<Long> staffIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.getEmailsOfStaffByStaffIds(staffIds));
    }

    @RequestMapping(value = "/access_roles", method = RequestMethod.GET)
    @ApiOperation("get access roles of staffs")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAccessRolesOfStaffByUserId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffRetrievalService.getAccessRolesOfStaffByUserId(unitId));
    }

    @RequestMapping(value = "/chat_server/register", method = RequestMethod.GET)
    @ApiOperation("register staffs to chat server")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> registerAllStaffsToChatServer() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.registerAllStaffsToChatServer());
    }


    @GetMapping(value = "/get_Staff_By_Unit")
    @ApiOperation("get staff by unit")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffByUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffRetrievalService.getStaffByUnit(unitId));
    }

    @PostMapping(value = "/details")
    @ApiOperation("get staff details  by ids")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffDetailByIds(@PathVariable Long unitId, @RequestBody Set<Long> staffIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffRetrievalService.getStaffDetailByIds(unitId, staffIds));
    }

    @GetMapping(value = "/staff_employment_by_unit_position/{unitPositionId}")
    @ApiOperation("get staff by unitPositionId")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffEmploymentDataByUnitPositionId(@RequestParam("type") String type, @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam(value = "reasonCodeIds", required = false) Set<Long> reasonCodeIds, @PathVariable long unitId,
                                                                                      @PathVariable Long unitPositionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffRetrievalService.getStaffEmploymentDataByUnitPositionId(startDate, unitPositionId, unitId, type, reasonCodeIds));
    }

    @RequestMapping(value = "/staff_list/chat", method = RequestMethod.GET)
    @ApiOperation("Get All staff List with login user staff id for chat purpose")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<ResponseDTO<StaffEmploymentWrapper>> getAllStaffListAndLoginUserStaffIdByUnitIdForChat(@PathVariable long unitId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, staffService.getStaffListAndLoginUserStaffIdByUnitId(unitId));
    }

    @RequestMapping(value = "/{staffId}/update_password", method = RequestMethod.PUT)
    @ApiOperation("update password")
    public ResponseEntity<Map<String, Object>> updatePassword(@PathVariable Long staffId,@Valid @RequestBody PasswordUpdateByAdminDTO passwordUpdateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, staffService.updatePasswordByManagement(staffId,passwordUpdateDTO));
    }

}
