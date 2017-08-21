package com.kairos.controller.access_group;
import com.kairos.persistence.model.user.access_permission.AccessGroup;
import com.kairos.persistence.model.user.access_permission.AccessGroupPermissionDTO;
import com.kairos.persistence.model.user.access_permission.AccessPermissionDTO;
import com.kairos.service.access_profile.AccessGroupService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by prabjot on 7/11/16.
 */
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(value = API_ORGANIZATION_UNIT_URL)
public class AccessGroupController {

    @Inject
    AccessGroupService accessGroupService;


    @RequestMapping(value = "/access_group", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createAccessGroup(@PathVariable long unitId, @RequestBody AccessGroup objectToSave) {
        AccessGroup savedObject = accessGroupService.createAccessGroup(unitId, objectToSave);
        if (savedObject == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);
        }
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, savedObject);
    }

    @RequestMapping(value = "/access_group/{accessGroupId}", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updateAccessGroup(@PathVariable long accessGroupId, @RequestBody AccessGroup accessGroup) {
        AccessGroup updatedObject = accessGroupService.updateAccessGroup(accessGroupId, accessGroup);
        if (updatedObject == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);
        }
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, updatedObject);
    }

    @RequestMapping(value = "/access_group/{accessGroupId}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteAccessGroup(@PathVariable long accessGroupId) {
        boolean isObjectDeleted = accessGroupService.deleteAccessGroup(accessGroupId);
        if (isObjectDeleted) {
            return ResponseHandler.generateResponse(HttpStatus.CREATED, true, isObjectDeleted);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, false);
    }


    @RequestMapping(value = "/access_group", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAccessGroups(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.getAccessGroups(unitId));
    }

    @RequestMapping(value = "/staff/{staffId}/access_group", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> assignAccessGroupToStaff(@PathVariable long staffId, @RequestBody Map<String, Object> reqData) {

        List<String> accessGroupIds = (List<String>) reqData.get("accessGroupIds");
        boolean isGroupAssigned = accessGroupService.assignAccessGroupToStaff(accessGroupIds, staffId);
        if (isGroupAssigned) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, isGroupAssigned);
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, isGroupAssigned);
    }

    @RequestMapping(value = "/access_page", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createAccessPage(@RequestBody Map<String, Object> reqData) {
        String name = (String) reqData.get("name");
        boolean isModule = (boolean) reqData.get("isModule");
        List<Map<String, Object>> childPages = (List<Map<String, Object>>) reqData.get("childPages");
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.createAccessPage(name, childPages, isModule));
    }

    @RequestMapping(value = "/user/{userId}/organization/{orgId}/access_modules", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAccessModulesForUnits(@PathVariable long userId, @PathVariable long orgId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.getAccessModulesForUnits(orgId, userId));
    }

    @RequestMapping(value = "/access_permission/unit_employment/{unitEmploymentId}/access_page/{accessPageId}", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> modifyAccessPagePermission(@PathVariable long unitEmploymentId,
                                                                          @PathVariable long accessPageId, @RequestBody Map<String, Object> map) {
        accessGroupService.modifyAccessPagePermission(unitEmploymentId, accessPageId, (boolean) map.get("read"));
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);
    }

    @RequestMapping(value = "/access_group/{accessGroupId}/access_page", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAccessPageHierarchy(@PathVariable long accessGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.getAccessPageHierarchy(accessGroupId));

    }

    @RequestMapping(value = "/access_group/{accessGroupId}/access_page", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> setAccessPageInGroup(@PathVariable long accessGroupId, @RequestBody AccessGroupPermissionDTO accessGroupPermission) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.setAccessPagePermissions(accessGroupId, accessGroupPermission.getAccessPageIds(), accessGroupPermission.isSelected()));
    }

    @RequestMapping(value = "/access_group/{accessGroupId}/auth/access_page", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAccessPageByAccessGroup(@RequestParam("unitId") long unitId, @RequestParam("staffId") long staffId,
                                                                          @PathVariable long accessGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accessGroupService.getAccessPageByAccessGroup(accessGroupId, unitId,staffId));

    }

    @RequestMapping(value = "/access_group/{accessGroupId}/auth/access_page", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> assignPermission(@PathVariable long accessGroupId, @RequestBody AccessPermissionDTO accessPermissionDTO) {
        accessGroupService.assignPermission(accessGroupId,accessPermissionDTO);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, true);

    }
}
