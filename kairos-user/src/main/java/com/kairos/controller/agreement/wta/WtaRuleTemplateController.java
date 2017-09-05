package com.kairos.controller.agreement.wta;

import com.kairos.response.dto.web.WTARuleTemplateDTO;
import com.kairos.response.dto.web.WtaRuleTemplateDTO;
import com.kairos.service.agreement.wta.WtaRuleTemplateService;
import com.kairos.util.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_COUNTRY_URL;

/**
 * Created by pawanmandhan on 5/8/17.
 */

@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@RestController
public class WtaRuleTemplateController {


    @Inject
    private WtaRuleTemplateService wtaRuleTemplateService;


    @RequestMapping(value = "/rule_templates", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> createRuleTemplate(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaRuleTemplateService.createRuleTemplate(countryId));
    }

    @RequestMapping(value = "/rule_templates", method = RequestMethod.GET)
    ResponseEntity<Map<String, Object>> getRuleTemplate(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaRuleTemplateService.getRuleTemplate(countryId));
    }

    @RequestMapping(value = "/rule_templates/{templateType}", method = RequestMethod.PUT)
    ResponseEntity<Map<String, Object>> getRuleTemplate(@PathVariable Long countryId, @PathVariable String templateType, @RequestBody WTARuleTemplateDTO templateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaRuleTemplateService.updateRuleTemplate(countryId,templateType, templateDTO));
    }

    @RequestMapping(value = "/rule_templates/category", method = RequestMethod.POST)
    ResponseEntity<Map<String,Object>> updateRuleTemplateCategory(@RequestBody WtaRuleTemplateDTO wtaRuleTemplateDTO, @PathVariable long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, wtaRuleTemplateService.updateRuleTemplateCategory(wtaRuleTemplateDTO,countryId));
    }





}
