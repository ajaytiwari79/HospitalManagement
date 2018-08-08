package com.kairos.controller.open_shift;

import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplateDTO;
import com.kairos.service.open_shift.OpenShiftRuleTemplateService;
import com.kairos.user.organization.OrgTypeAndSubTypeDTO;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

@RestController
@Api(API_ORGANIZATION_URL)
@RequestMapping(API_ORGANIZATION_URL)
public class OpenShiftRuleTemplateController {
    @Inject
    OpenShiftRuleTemplateService openShiftRuleTemplateService;

    @ApiOperation("Create Rule template for automatic open shift")
    @PostMapping(value = COUNTRY_URL+"/open_shift/rule_template")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createRuleTemplateForOpenShift(@PathVariable Long countryId, @RequestBody OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftRuleTemplateService.createRuleTemplateForOpenShift(countryId,openShiftRuleTemplateDTO));
    }

    @ApiOperation("Get all Rule templates based on countryId")
    @GetMapping(value = COUNTRY_URL+"/open_shift/rule_templates")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getOpenShiftRuleTemplate(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftRuleTemplateService.findAllRuleTemplateForOpenShift(countryId));
    }

    @ApiOperation("Update Rule templates ")
    @PutMapping(value = COUNTRY_URL+"/open_shift/rule_template/{ruleTemplateId}")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateRuleTemplateForOpenShift(@PathVariable Long countryId, @PathVariable BigInteger ruleTemplateId, @RequestBody OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftRuleTemplateService.updateRuleTemplateForOpenShift(countryId,ruleTemplateId, openShiftRuleTemplateDTO));
    }

    @ApiOperation("delete Rule template based on countryId")
    @DeleteMapping(value = COUNTRY_URL+"/open_shift/rule_template/{ruleTemplateId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteOpenShiftRuleTemplate(@PathVariable Long countryId, @PathVariable BigInteger ruleTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftRuleTemplateService.deleteOpenShiftRuleTemplate(countryId,ruleTemplateId));
    }

    @ApiOperation("Get  Rule template based on ruleTemplateId")
    @GetMapping(value = COUNTRY_URL+"/open_shift/rule_template/{ruleTemplateId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getRuleTemplateByIdAtCountry(@PathVariable Long countryId,@PathVariable BigInteger ruleTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftRuleTemplateService.getRuleTemplateAndPriorityGroupByIdAtCountry(ruleTemplateId,countryId));
    }

    @ApiOperation("Copy Rule templates for Unit")
    @PostMapping(value = UNIT_URL+"/open_shift/rule_template")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> createRuleTemplateForUnit(@PathVariable Long unitId, @RequestBody OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftRuleTemplateService.createRuleTemplateForUnit(unitId,openShiftRuleTemplateDTO));
    }

    @ApiOperation("Copy Rule templates for Unit")
    @PostMapping(value = UNIT_URL+"/open_shift/copy_rule_template")
    //  @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> copyOpenShiftRuleTemplateInUnit(@PathVariable Long unitId, @RequestBody OrgTypeAndSubTypeDTO orgTypeAndSubTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftRuleTemplateService.copyOpenShiftRuleTemplateInUnit(unitId,orgTypeAndSubTypeDTO));
    }

    @ApiOperation("Get all Rule templates based on unitId")
    @GetMapping(value = UNIT_URL+"/open_shift/rule_templates")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getRuleTemplatesOfUnit(@PathVariable long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftRuleTemplateService.getRuleTemplatesOfUnit(unitId));
    }

    @ApiOperation("Update Rule template")
    @PutMapping(value = UNIT_URL+"/open_shift/rule_template/{ruleTemplateId}")
        //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    ResponseEntity<Map<String, Object>> updateRuleTemplateOfUnit(@PathVariable Long unitId, @PathVariable BigInteger ruleTemplateId, @RequestBody OpenShiftRuleTemplateDTO openShiftRuleTemplateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftRuleTemplateService.updateRuleTemplateOfUnit(unitId,ruleTemplateId, openShiftRuleTemplateDTO));
    }

    @ApiOperation("delete Rule template based on countryId")
    @DeleteMapping(value = UNIT_URL+"/open_shift/rule_template/{ruleTemplateId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> deleteRuleTemplateOfUnit(@PathVariable Long unitId, @PathVariable BigInteger ruleTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftRuleTemplateService.deleteRuleTemplateOfUnit(ruleTemplateId,unitId));
    }

    @ApiOperation("Get  Rule template based on ruleTemplateId")
    @GetMapping(value = UNIT_URL+"/open_shift/rule_template/{ruleTemplateId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getRuleTemplateById(@PathVariable Long unitId,@PathVariable BigInteger ruleTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftRuleTemplateService.getRuleTemplateAndPriorityGroupByIdAtUnit(ruleTemplateId,unitId));
    }

    @ApiOperation("Get  Rule templates by ActivityId")
    @GetMapping(value = UNIT_URL+"/rule_template/activity/{activityId}")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> findByUnitIdAndActivityId(@PathVariable Long unitId,@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, openShiftRuleTemplateService.findByUnitIdAndActivityId(activityId,unitId));
    }

}
