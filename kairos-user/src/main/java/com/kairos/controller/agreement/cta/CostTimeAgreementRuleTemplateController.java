package com.kairos.controller.agreement.cta;

import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplateDTO;
import com.kairos.service.agreement.cta.CostTimeAgreementService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_COUNTRY_URL;
@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
public class CostTimeAgreementRuleTemplateController {
    private @Autowired
    CostTimeAgreementService costTimeAgreementService;

    /**
     * @auther anil maurya
     * @param countryId
     * @return
     */
    @RequestMapping(value = "/cta/rule-templates", method = RequestMethod.GET)
    @ApiOperation("get CTA rule template")
    public ResponseEntity<Map<String, Object>> getAllCTARuleTemplate(@PathVariable Long countryId ) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,costTimeAgreementService.loadAllCTARuleTemplateByCountry(countryId));
    }

    /**
     * @auther anil maurya
     * @param countryId
     * @return
     */
    @RequestMapping(value = "/cta/rule-template/{id}", method = RequestMethod.PUT)
    @ApiOperation("get CTA rule template")
    public ResponseEntity<Map<String, Object>> updateCTARuleTemplate(@PathVariable Long countryId
            ,@RequestBody @Valid CTARuleTemplateDTO ctaRuleTemplateDTO,@PathVariable Long id ) throws ExecutionException, InterruptedException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,costTimeAgreementService.updateCTARuleTemplate(countryId,id,ctaRuleTemplateDTO));
    }


}
