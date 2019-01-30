package com.kairos.controller.organization;

import com.kairos.persistence.model.common.QueryResult;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.service.organization.GroupService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by oodles on 18/10/16.
 */

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL + "/group")
public class GroupController {

    @Inject
    private GroupService groupService;

    // Organization
    @ApiOperation(value = "Get All Groups of Organization")
    @RequestMapping(value = "/group", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllGroupOfOrganization(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                groupService.getGroups(unitId));

    }


    @ApiOperation(value = "Get Group of Organization by groupId")
    @RequestMapping(value = "/{groupId}", method = RequestMethod.GET)
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getGroupOfOrganizationById(@PathVariable Long unitId, @PathVariable Long groupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                groupService.getGroupOfOrganizationById(unitId, groupId));


    }

    @ApiOperation(value = "Add Group to Organization")
    @RequestMapping(method = RequestMethod.POST)
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createGroup(@PathVariable long unitId, @Validated @RequestBody Group group) {

        QueryResult queryResult = groupService.createGroup(group, unitId);
        if (queryResult == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false,
                    false);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, queryResult);

    }

    ///Skills
    @ApiOperation(value = "Get Group Available Skills")
    @RequestMapping(value = "/skill/available/{groupId}", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getGroupAvailableSkills(@PathVariable Long groupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.getGroupAvailableSkills(groupId));
    }

    @ApiOperation(value = "Get Group Selected Skills")
    @RequestMapping(value = "/skill/{groupId}", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getGroupSelectedSkills(@PathVariable Long groupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.getGroupSelectedSkills(groupId));
    }

    @ApiOperation(value = "Add Skill to Group")
    @RequestMapping(value = "/skill/{groupId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addGroupSkills(@PathVariable Long groupId, @RequestBody Map<String, Long[]> data) {
        Long[] skillIds = data.get("data");
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.addGroupSelectedSkills(groupId, skillIds));
    }


    // Services
    @ApiOperation(value = "Get Group Available Service")
    @RequestMapping(value = "/service/available/{groupId}", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getGroupAvailableService(@PathVariable Long groupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.getGroupAvailableService(groupId));

    }

    @ApiOperation(value = "Get Group Selected Service")
    @RequestMapping(value = "/service/{groupId}", method = RequestMethod.GET)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getGroupSelectedService(@PathVariable Long groupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.getGroupSelectedService(groupId));

    }

    @ApiOperation(value = "Add Service to Group")
    @RequestMapping(value = "/service/{groupId}", method = RequestMethod.PUT)
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addGroupService(@PathVariable Long groupId, @RequestBody Map<String, Long[]> data) {
        Long[] serviceIds = data.get("data");
        return ResponseHandler.generateResponse(HttpStatus.OK, true, this.groupService.addGroupSelectedService(groupId, serviceIds));
    }



}
