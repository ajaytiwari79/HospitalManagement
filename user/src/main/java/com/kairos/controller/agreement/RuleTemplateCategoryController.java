package com.kairos.controller.agreement;

import org.springframework.web.bind.annotation.*;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;

/**
 * Created by vipul on 2/8/17.
 * Performs CRUD operations on Rule Template category
 */


@RequestMapping(API_ORGANIZATION_URL)
@RestController
public class RuleTemplateCategoryController {
    /*@Inject
    private RuleTemplateCategoryService ruleTemplateCategoryService;
    @Inject
    private CountryService countryService;


    @RequestMapping(value = COUNTRY_URL+"/template_category", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> createRuleTemplate(@PathVariable long countryId, @RequestBody @Valid RuleTemplateCategory ruleTemplateCategory) {

        if (ruleTemplateCategory != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateCategoryService.createRuleTemplateCategory(countryId, ruleTemplateCategory));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @RequestMapping(value = COUNTRY_URL+"/template_category/{ruleTemplateCategoryType}", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getRulesTemplate(@PathVariable long countryId,@PathVariable RuleTemplateCategoryType ruleTemplateCategoryType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateCategoryService.getRulesTemplateCategory(countryId,ruleTemplateCategoryType));
    }

    @RequestMapping(value = COUNTRY_URL+"/template_category/{templateCategoryId}", method = RequestMethod.DELETE)
    ResponseEntity<Map<String, Object>> deleteRuleTemplate(@PathVariable long countryId, @PathVariable long templateCategoryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateCategoryService.deleteRuleTemplateCategory(countryId, templateCategoryId));
    }

    @RequestMapping(value = COUNTRY_URL+"/template_category/{templateCategoryId}", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateRuleTemplateCategory(@PathVariable long countryId, @PathVariable long templateCategoryId, @RequestBody UpdateRuleTemplateCategoryDTO ruleTemplateCategory) {
        Map<String, Object> updatedRuleTemplate = ruleTemplateCategoryService.updateRuleTemplateCategory(countryId, templateCategoryId, ruleTemplateCategory);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, updatedRuleTemplate);
    }
    @RequestMapping(value = COUNTRY_URL+"/rule_templates/category", method = RequestMethod.POST)
    ResponseEntity<Map<String,Object>> updateRuleTemplateCategory(@Valid @RequestBody RuleTemplateCategoryCTADTO ruleTemplateCategory, @PathVariable long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateCategoryService.createRuleTemplateCategory(countryId,ruleTemplateCategory));
    }*/



}
