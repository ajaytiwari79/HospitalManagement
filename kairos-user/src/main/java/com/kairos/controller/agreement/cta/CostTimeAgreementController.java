package com.kairos.controller.agreement.cta;

import com.kairos.response.dto.web.cta.CollectiveTimeAgreementDTO;
import com.kairos.service.agreement.cta.CostTimeAgreementService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;

@RequestMapping(API_ORGANIZATION_URL)
@RestController
public class CostTimeAgreementController {
    @Autowired
  private  CostTimeAgreementService costTimeAgreementService;

    /**
     * @auther anil maurya
     * @param countryId
     * @return
     */
    @RequestMapping(value = "/country/{countryId}/cta/", method = RequestMethod.POST)
    @ApiOperation("Create CTA")
    public ResponseEntity<Map<String, Object>> updateCTARuleTemplate(@PathVariable Long countryId
            , @RequestBody @Valid CollectiveTimeAgreementDTO collectiveTimeAgreementDTO ) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,
                costTimeAgreementService.createCostTimeAgreement(countryId,collectiveTimeAgreementDTO));
    }
}
