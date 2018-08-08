package com.kairos.controller.cta;

import com.kairos.activity.cta.CTARuleTemplateDTO;
import com.kairos.activity.cta.CollectiveTimeAgreementDTO;
import com.kairos.service.cta.CostTimeAgreementService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.UNIT_URL;

/**
 * @author pradeep
 * @date - 31/7/18
 */

@RestController
@RequestMapping(API_ORGANIZATION_URL)
public class CostTimeAgreementRuleTemplateController {
    @Inject private
    CostTimeAgreementService costTimeAgreementService;

    /**
     *
     * @param countryId
     * @return
     */
    @RequestMapping(value = "/country/{countryId}/cta/rule-templates", method = RequestMethod.GET)
    @ApiOperation("get CTA rule template")
    public ResponseEntity<Map<String, Object>> getAllCTARuleTemplate(@PathVariable Long countryId ) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,costTimeAgreementService.loadAllCTARuleTemplateByCountry(countryId));
    }

    /**
     *
     * @param countryId
     * @param ctaRuleTemplateDTO
     * @param id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @RequestMapping(value = "/country/{countryId}/cta/rule-template/{id}", method = RequestMethod.PUT)
    @ApiOperation("get CTA rule template")
    public ResponseEntity<Map<String, Object>> updateCTARuleTemplate(@PathVariable Long countryId
            , @RequestBody @Valid CTARuleTemplateDTO ctaRuleTemplateDTO, @PathVariable BigInteger id ) throws ExecutionException, InterruptedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,costTimeAgreementService.updateCTARuleTemplate(countryId,id,ctaRuleTemplateDTO));
    }

    /**
     *
     * @param unitId
     * @return
     */
    @RequestMapping(value = "/unit/{unitId}/cta/rule-templates", method = RequestMethod.GET)
    @ApiOperation("get CTA rule template of Unit")
    public ResponseEntity<Map<String, Object>> getAllCTARuleTemplateForUnit(@PathVariable Long unitId ) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,costTimeAgreementService.loadAllCTARuleTemplateByUnit(unitId));
    }

}
