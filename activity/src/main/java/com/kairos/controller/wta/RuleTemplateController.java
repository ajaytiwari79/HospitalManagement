package com.kairos.controller.wta;

import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.dto.activity.wta.rule_template_category.RuleTemplateCategoryRequestDTO;
import com.kairos.persistence.model.wta.templates.WTABuilderService;
import com.kairos.service.wta.RuleTemplateCategoryService;
import com.kairos.service.wta.RuleTemplateService;
import com.kairos.utils.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;


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
    @Inject
    private WTABuilderService wtaBuilderService;

    @RequestMapping(value = COUNTRY_URL + "/rule_templates", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> createRuleTemplate(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateService.createRuleTemplate(countryId));
    }

    @RequestMapping(value = COUNTRY_URL + "/rule_templates", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getRuleTemplate(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateService.getRuleTemplate(countryId));
    }

    @RequestMapping(value = COUNTRY_URL + "/rule_templates/{ruleTemplateId}", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> getRuleTemplate(@PathVariable Long countryId, @PathVariable BigInteger ruleTemplateId, @RequestBody WTABaseRuleTemplateDTO wtaBaseRuleTemplateDTO) {
        // WTABaseRuleTemplateDTO wtaBaseRuleTemplateDTO = WTABuilderService.copyRuleTemplateMapToDTO(template);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateService.updateRuleTemplate(countryId, ruleTemplateId,wtaBaseRuleTemplateDTO));
    }


    @RequestMapping(value = COUNTRY_URL + "/rule_templates/category", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> updateRuleTemplateCategory(@Valid @RequestBody RuleTemplateCategoryRequestDTO ruleTemplateDTO, @PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateCategoryService.createRuleTemplateCategory(countryId, ruleTemplateDTO));
    }

    @RequestMapping(value = UNIT_URL + "/rule_templates", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getRulesTemplateCategoryByUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateService.getRulesTemplateCategoryByUnit(unitId));
    }

    @RequestMapping(value = COUNTRY_URL + "/copy_rule_template", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> copyRuleTemplate(@PathVariable Long countryId, @RequestBody WTABaseRuleTemplateDTO template) {
        //WTABaseRuleTemplateDTO wtaBaseRuleTemplateDTO = WTABuilderService.copyRuleTemplateMapToDTO(template);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateService.copyRuleTemplate(countryId, template));
    }


}
