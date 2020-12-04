package com.kairos.controller.organization;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.persistence.model.organization.group.GroupDTO;
import com.kairos.service.organization.GroupService;
import com.kairos.service.translation.TranslationService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
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

    @Inject private TranslationService translationService;

    @ApiOperation(value = "Add Group in Unit")
    @PostMapping(value = "/group")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createGroup(@PathVariable Long unitId, @Validated @RequestBody GroupDTO groupDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.createGroup(unitId, groupDTO));
    }

    @ApiOperation(value = "Update Group of Unit")
    @PutMapping(value = "/group/{groupId}")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> updateGroup(@PathVariable Long unitId, @PathVariable Long groupId, @RequestBody GroupDTO groupDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.updateGroup(unitId, groupId, groupDTO));
    }

    @ApiOperation(value = "Delete Group of Unit")
    @DeleteMapping(value = "/group/{groupId}")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteGroup(@PathVariable Long groupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.deleteGroup(groupId));
    }

    @ApiOperation(value = "Get Group Details")
    @GetMapping(value = "/group/{groupId}")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getGroupDetails(@PathVariable Long groupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.getGroupDetail( groupId));
    }

    @ApiOperation(value = "Get Group's Staff Details")
    @PostMapping(value = "/group/staffs")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffListByGroupFilter(@PathVariable Long unitId, @RequestBody List<FilterSelectionDTO> filtersData) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.getStaffListByGroupFilter(unitId, filtersData));
    }

    @ApiOperation(value = "Get all Groups of unit")
    @GetMapping(value = "/groups")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllGroupByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.getAllGroupsOfUnit(unitId));
    }

    @ApiOperation(value = "Get all StaffIds")
    @PostMapping(value = "/groups")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllStaffByGroupIds(@PathVariable Long unitId, @RequestBody List<Long> groupIds) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, groupService.getAllStaffIdsByGroupIds(unitId, groupIds));
    }

    @PutMapping(value = "/group/{id}/language_settings")
    @ApiOperation("Add translated data")
        //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateTranslationsOfRelationType(@PathVariable Long id, @RequestBody Map<String, TranslationInfo> translations) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, translationService.updateTranslation(id,translations));
    }
}
