package com.kairos.activity.controller.priority_group;

import com.kairos.activity.persistence.model.priority_group.PriorityGroupDTO;
import com.kairos.activity.service.priority_group.PriorityGroupService;
import com.kairos.activity.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.*;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class PriorityGroupController {
    @Inject
    PriorityGroupService priorityGroupService;

    @ApiOperation("Create Priority Group")
    @PostMapping(value = COUNTRY_URL+"/priority_group")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createPriorityGroup(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.createPriorityGroupForCountry(countryId));
    }

    @ApiOperation("Get all Priority Group based on countryId")
    @GetMapping(value = COUNTRY_URL+"/priority_groups")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPriorityGroups(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.findAllPriorityGroups(countryId));
    }

    @ApiOperation("Update Priority Group")
    @PutMapping(value = COUNTRY_URL+"/priority_group/{priorityGroupId}")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updatePriorityGroup(@PathVariable Long countryId, @PathVariable BigInteger priorityGroupId, @RequestBody PriorityGroupDTO priorityGroupDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.updatePriorityGroup(countryId,priorityGroupId, priorityGroupDTO));
    }

    @ApiOperation("delete Priority Group based on countryId")
    @DeleteMapping(value = COUNTRY_URL+"/priority_group/{priorityGroupId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deletePriorityGroup(@PathVariable Long countryId, @PathVariable BigInteger priorityGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.deletePriorityGroup(countryId,priorityGroupId));
    }

    @ApiOperation("Copy Priority Group for Units")
    @PostMapping(value = UNIT_URL+"/copy_priority_group")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> copyPriorityGroupsForUnit(@PathVariable Long unitId,@RequestParam("countryId") Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.copyPriorityGroupsForUnit(unitId,countryId));
    }

    @ApiOperation("Get all Priority Group based on unitId")
    @GetMapping(value = UNIT_URL+"/priority_groups")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPriorityGroupsOfUnit(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.getPriorityGroupsOfUnit(unitId));
    }

    @ApiOperation("Update Priority Group")
    @PutMapping(value = UNIT_URL+"/priority_group/{priorityGroupId}")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updatePriorityGroupOfUnit(@PathVariable Long unitId, @PathVariable BigInteger priorityGroupId, @RequestBody PriorityGroupDTO priorityGroupDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.updatePriorityGroupOfUnit(unitId,priorityGroupId, priorityGroupDTO));
    }

    @ApiOperation("delete Priority Group based on countryId")
    @DeleteMapping(value = UNIT_URL+"/priority_group/{priorityGroupId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deletePriorityGroupOfUnit(@PathVariable Long unitId, @PathVariable BigInteger priorityGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.deletePriorityGroupOfUnit(unitId,priorityGroupId));
    }

    @ApiOperation("Copy Priority Group for Units")
    @PostMapping(value = UNIT_URL+"/order/{orderId}/copy_priority_group")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> copyPriorityGroupsForOrder(@PathVariable Long unitId,@PathVariable BigInteger orderId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.copyPriorityGroupsForOrder(unitId,orderId));
    }

    @ApiOperation("Get  Priority Group based on priorityGroupId")
    @GetMapping(value = COUNTRY_URL+"/priority_group/{priorityGroupId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPriorityGroupById(@PathVariable BigInteger priorityGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.getPriorityGroupById(priorityGroupId));
    }


}
