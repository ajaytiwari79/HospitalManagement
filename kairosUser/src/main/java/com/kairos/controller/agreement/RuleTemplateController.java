package com.kairos.controller.agreement;

import com.kairos.persistence.model.user.agreement.wta.RuleTemplateCategoryDTO;
import com.kairos.response.dto.web.RuleTemplateDTO;
import com.kairos.service.agreement.RuleTemplateCategoryService;
import com.kairos.service.agreement.RuleTemplateService;
import com.kairos.util.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;
import static com.kairos.constants.ApiConstants.UNIT_URL;

/**
 * Created by pawanmandhan on 5/8/17.
 */

@RequestMapping(API_ORGANIZATION_URL)
@RestController
public class RuleTemplateController {


    /*@Inject
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
    ResponseEntity<Map<String, Object>> getRuleTemplate(@PathVariable Long countryId, @PathVariable String templateType, @Valid @RequestBody RuleTemplateCategoryDTO templateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateService.updateRuleTemplate(countryId, templateDTO));
    }


    @RequestMapping(value = COUNTRY_URL+"/rule_templates/category", method = RequestMethod.POST)
    ResponseEntity<Map<String,Object>> updateRuleTemplateCategory(@Valid @RequestBody RuleTemplateDTO ruleTemplateDTO, @PathVariable long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateCategoryService.updateRuleTemplateCategory(ruleTemplateDTO,countryId));
    }

    @RequestMapping(value = UNIT_URL+"/rule_templates", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getRulesTemplateCategoryByUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateCategoryService.getRulesTemplateCategoryByUnit(unitId));
    }

    @RequestMapping(value = COUNTRY_URL+"/copy_rule_template", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> copyRuleTemplate(@PathVariable Long countryId, @Valid @RequestBody RuleTemplateCategoryDTO templateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateService.copyRuleTemplate(countryId,templateDTO));
    }*/


}
