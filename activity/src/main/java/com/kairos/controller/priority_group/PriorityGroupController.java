package com.kairos.controller.priority_group;

import com.kairos.service.priority_group.PriorityGroupService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class PriorityGroupController {
    @Inject
    private PriorityGroupService priorityGroupService;

    @ApiOperation("Create Priority Group")
    @PostMapping(value = COUNTRY_URL+"/priority_groups")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createPriorityGroup(@PathVariable Long countryId, @RequestBody List<PriorityGroupDTO> priorityGroupDTOs) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.createPriorityGroupForCountry(countryId,priorityGroupDTOs));
    }

    @ApiOperation("Get all Priority Group based on countryId")
    @GetMapping(value = COUNTRY_URL+"/priority_groups")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPriorityGroups(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.findAllPriorityGroups(countryId));
    }

    @ApiOperation("Update Priority Group")
    @PutMapping(value = COUNTRY_URL+"/priority_groups/{priorityGroupId}")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updatePriorityGroup(@PathVariable Long countryId, @PathVariable BigInteger priorityGroupId, @RequestBody PriorityGroupDTO priorityGroupDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.updatePriorityGroup(countryId,priorityGroupId, priorityGroupDTO));
    }

    @ApiOperation("delete Priority Group based on countryId")
    @DeleteMapping(value = COUNTRY_URL+"/priority_groups/{priorityGroupId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deletePriorityGroup(@PathVariable Long countryId, @PathVariable BigInteger priorityGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.deletePriorityGroup(countryId,priorityGroupId));
    }

    @ApiOperation("Copy Priority Group for Units")
    @PostMapping(value = UNIT_URL+"/priority_groups")
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
    @PutMapping(value = UNIT_URL+"/priority_groups/{priorityGroupId}")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updatePriorityGroupOfUnit(@PathVariable Long unitId, @PathVariable BigInteger priorityGroupId, @RequestBody PriorityGroupDTO priorityGroupDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.updatePriorityGroupOfUnit(unitId,priorityGroupId, priorityGroupDTO));
    }

    @ApiOperation("delete Priority Group based on unitId")
    @DeleteMapping(value = UNIT_URL+"/priority_groups/{priorityGroupId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deletePriorityGroupOfUnit(@PathVariable Long unitId, @PathVariable BigInteger priorityGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.deletePriorityGroupOfUnit(unitId,priorityGroupId));
    }

    @ApiOperation("Copy Priority Group for Orders")
    @PostMapping(value = UNIT_URL+"/order/{orderId}/priority_groups")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> copyPriorityGroupsForOrder(@PathVariable Long unitId,@PathVariable BigInteger orderId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.copyPriorityGroupsForOrder(unitId,orderId));
    }

    @ApiOperation("Get  Priority Group based on priorityGroupId")
    @GetMapping(value = COUNTRY_URL+"/priority_groups/{priorityGroupId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPriorityGroupOfCountryById(@PathVariable Long countryId,@PathVariable BigInteger priorityGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.getPriorityGroupOfCountryById(countryId,priorityGroupId));
    }

    @ApiOperation("Get  Priority  Group of unit based on priorityGroupId")
    @GetMapping(value = UNIT_URL+"/priority_groups/{priorityGroupId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPriorityGroupOfUnitById(@PathVariable Long unitId,@PathVariable BigInteger priorityGroupId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.getPriorityGroupOfUnitById(unitId,priorityGroupId));
    }

    @ApiOperation("Get  Priority  Group  based on ruleTemplateId")
    @GetMapping(value = UNIT_URL+"/priority_groups/rule_template/{ruleTemplateId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPriorityGroupsByRuleTemplate(@PathVariable Long unitId,@PathVariable BigInteger ruleTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.getPriorityGroupsByRuleTemplateForUnit(unitId,ruleTemplateId));
    }

    @ApiOperation("Get  Priority  Group  based on orderId")
    @GetMapping(value = UNIT_URL+"/priority_groups/order/{orderId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getPriorityGroupsByOrderId(@PathVariable Long unitId,@PathVariable BigInteger orderId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, priorityGroupService.getPriorityGroupsByOrderIdForUnit(unitId,orderId));
    }
    @ApiOperation("Get Staffs filtered by priorty group rules")
    @GetMapping(value = UNIT_URL+"/priority_groups/{priorityGroupId}/filterPriorityGroup")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getStaffByPriorityGroups(@PathVariable Long unitId,@PathVariable BigInteger priorityGroupId) {
        priorityGroupService.notifyStaffByPriorityGroup(priorityGroupId);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }

}