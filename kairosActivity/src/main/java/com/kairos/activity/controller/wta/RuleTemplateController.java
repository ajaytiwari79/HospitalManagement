package com.kairos.activity.controller.wta;

import com.kairos.activity.service.wta.RuleTemplateCategoryService;
import com.kairos.activity.service.wta.RuleTemplateService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.response.dto.web.wta.RuleTemplateCategoryDTO;
import com.kairos.response.dto.web.wta.WTARuleTemplateDTO;
import com.kairos.response.dto.web.wta.RuleTemplateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.*;


/**
 * Created by pawanmandhan on 5/8/17.
 */

@RequestMapping(API_ORGANIZATION_URL)
@RestController
public class RuleTemplateController {


    @Inject
    private RuleTemplateService ruleTemplateService;
    @Inject
    private RuleTemplateCategoryService ruleTemplateCategoryService;

    @RequestMapping(value = COUNTRY_URL+"/rule_templates", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> createRuleTemplate(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateService.createRuleTemplate(countryId));
    }

    @RequestMapping(value = COUNTRY_URL+"/rule_templates", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getRuleTemplate(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateService.getRuleTemplate(countryId));
    }

    @RequestMapping(value = COUNTRY_URL+"/rule_templates/{templateType}", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> getRuleTemplate(@PathVariable Long countryId, @PathVariable String templateType, @Valid @RequestBody WTARuleTemplateDTO templateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateService.updateRuleTemplate(countryId, templateDTO));
    }


    @RequestMapping(value = COUNTRY_URL+"/rule_templates/category", method = RequestMethod.POST)
    ResponseEntity<Map<String,Object>> updateRuleTemplateCategory(@Valid @RequestBody RuleTemplateCategoryDTO ruleTemplateDTO, @PathVariable long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateCategoryService.createRuleTemplateCategory(countryId,ruleTemplateDTO));
    }

    @RequestMapping(value = UNIT_URL+"/rule_templates", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getRulesTemplateCategoryByUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateCategoryService.getRulesTemplateCategoryByUnit(unitId));
    }

    @RequestMapping(value = COUNTRY_URL+"/copy_rule_template", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> copyRuleTemplate(@PathVariable Long countryId, @Valid @RequestBody WTARuleTemplateDTO templateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateService.copyRuleTemplate(countryId,templateDTO));
    }


}
