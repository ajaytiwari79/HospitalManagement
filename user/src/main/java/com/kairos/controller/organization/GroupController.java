package com.kairos.controller.organization;

import com.kairos.persistence.model.organization.group.GroupDTO;
import com.kairos.persistence.model.organization.team.TeamDTO;
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
 * Created By G.P.Ranjan on 19/11/19
 **/
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
public class GroupController {
    @Inject
    private GroupService groupService;

    @ApiOperation(value = "Add Group in Unit")
    @PostMapping(value = "/group")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createGroup(@PathVariable Long unitId, @Validated @RequestBody GroupDTO groupDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.createGroup(unitId, groupDTO));
    }

    @ApiOperation(value = "Update Group of Unit")
    @PutMapping(value = "/group/{groupId}")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateGroup(@PathVariable Long unitId, @PathVariable Long groupId, @Validated @RequestBody GroupDTO groupDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.updateGroup(unitId, groupId, groupDTO));
    }

    @ApiOperation(value = "Get Group Details")
    @GetMapping(value = "/group/{groupId}")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getGroupDetails(@PathVariable Long teamId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.getGroupDetails( teamId));
    }

    @ApiOperation(value = "Get all Groups of unit")
    @GetMapping(value = "/groups")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllGroupByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.getAllGroupsOfUnit( unitId));
    }
}
