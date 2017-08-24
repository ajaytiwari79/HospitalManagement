package com.kairos.controller.cta_wta.template;

import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.service.country.CountryService;
import com.kairos.service.cta_wta.template.RuleTemplateCategoryService;
import com.kairos.util.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_COUNTRY_URL;


/**
 * Created by vipul on 2/8/17.
 * Performs CRUD operations on Rule Template category
 */


@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@RestController
public class RuleTemplateCategoryController {
    @Inject
    private RuleTemplateCategoryService ruleTemplateCategoryService;
    @Inject
    private CountryService countryService;


    @RequestMapping(value = "/template_category", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> createRuleTemplate(@PathVariable long countryId, @RequestBody RuleTemplateCategory ruleTemplateCategory) {

        if (ruleTemplateCategory != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateCategoryService.createRuleTemplate(countryId, ruleTemplateCategory));
        }
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, false, null);
    }

    @RequestMapping(value = "/template_category", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getRulesTemplate(@PathVariable long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateCategoryService.getRulesTemplate(countryId));
    }

    @RequestMapping(value = "/template_category/{templateCategoryId}", method = RequestMethod.DELETE)
    ResponseEntity<Map<String, Object>> deleteRuleTemplate(@PathVariable long countryId, @PathVariable long templateCategoryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateCategoryService.deleteRuleTemplateCategory(countryId, templateCategoryId));
    }

    @RequestMapping(value = "/template_category/{templateCategoryId}", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateRuleTemplateCategory(@PathVariable long countryId, @PathVariable long templateCategoryId, @RequestBody RuleTemplateCategory ruleTemplateCategory) {
        Map<String, Object> updatedRuleTemplate = ruleTemplateCategoryService.updateRuleTemplateCategory(countryId, templateCategoryId, ruleTemplateCategory);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, updatedRuleTemplate);
    }
}
