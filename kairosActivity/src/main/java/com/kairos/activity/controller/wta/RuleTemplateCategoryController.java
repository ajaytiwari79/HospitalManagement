package com.kairos.activity.controller.wta;

import com.kairos.activity.service.wta.RuleTemplateCategoryService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.response.dto.web.UpdateRuleTemplateCategoryDTO;

import com.kairos.response.dto.web.enums.RuleTemplateCategoryType;
import com.kairos.response.dto.web.wta.RuleTemplateCategoryDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.activity.constants.ApiConstants.*;


/**
 * Created by vipul on 2/8/17.
 * Performs CRUD operations on Rule Template category
 */


@RequestMapping(API_ORGANIZATION_URL)
@RestController
public class RuleTemplateCategoryController {
    @Inject
    private RuleTemplateCategoryService ruleTemplateCategoryService;


    @RequestMapping(value = COUNTRY_URL+"/template_category", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> createRuleTemplate(@PathVariable long countryId, @RequestBody @Valid RuleTemplateCategoryDTO ruleTemplateCategory) {

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
    ResponseEntity<Map<String, Object>> deleteRuleTemplate(@PathVariable long countryId, @PathVariable BigInteger templateCategoryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, ruleTemplateCategoryService.deleteRuleTemplateCategory(countryId, templateCategoryId));
    }

    @RequestMapping(value = COUNTRY_URL+"/template_category/{templateCategoryId}", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> updateRuleTemplateCategory(@PathVariable long countryId, @PathVariable BigInteger templateCategoryId, @RequestBody RuleTemplateCategoryDTO ruleTemplateCategory) {
        RuleTemplateCategoryDTO updatedRuleTemplate = ruleTemplateCategoryService.updateRuleTemplateCategory(countryId, templateCategoryId, ruleTemplateCategory,null);
        return ResponseHandler.generateResponse(HttpStatus.OK, true, updatedRuleTemplate);
    }


}
