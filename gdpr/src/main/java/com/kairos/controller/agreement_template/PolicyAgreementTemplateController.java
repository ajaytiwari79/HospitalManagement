package com.kairos.controller.agreement_template;

import com.kairos.gdpr.PolicyAgreementTemplateDTO;
import com.kairos.service.agreement_template.AgreementSectionService;
import com.kairos.service.agreement_template.PolicyAgreementTemplateService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
/*
 *
 *  created by bobby 10/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class PolicyAgreementTemplateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyAgreementTemplateController.class);

    @Inject
    private PolicyAgreementTemplateService policyAgreementTemplateService;


    @ApiOperation("create Agreement Template")
    @RequestMapping(value = "/agreement_template", method = RequestMethod.POST)
    public ResponseEntity<Object> createPolicyAgreementTemplate(@PathVariable Long countryId, @PathVariable Long organizationId, @Validated @RequestBody PolicyAgreementTemplateDTO agreementTemplateDto) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id cannot be null ");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id cannot be null ");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.createBasicPolicyAgreementTemplate(countryId, organizationId, agreementTemplateDto));

    }

    @ApiOperation("get All agreement sections and Subjection of Agreement template ")
    @GetMapping(value = "/agreement_template/{agreementTemplateId}/section")
    public ResponseEntity<Object> getAllAgreementSectionWithSubSectionsAndClausesOfAgreementTemplate(@PathVariable Long countryId, @PathVariable Long organizationId,@PathVariable BigInteger agreementTemplateId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        else if (agreementTemplateId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, " Agreement Template  id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.getAllAgreementSectionsAndSubSectionsOfAgreementTemplateByTemplateId(countryId,organizationId, agreementTemplateId));
    }


    @ApiOperation("delete Policy agreement Template By Id")
    @DeleteMapping("/agreement_template/delete/{id}")
    public ResponseEntity<Object> deletePolicyAgreementTemplateById(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id cannot be null ");
        } else if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Agreement template id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.deletePolicyAgreementTemplate(countryId, organizationId, id));

    }


    @ApiOperation("get All policy agreement Template with sections and Clauses ")
    @GetMapping("/agreement_template")
    public ResponseEntity<Object> getPolicyAgreementTemplateWithData(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id cannot be null ");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id cannot be null ");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.getAllPolicyAgreementTemplateWithAgreementSectionAndClauses(countryId, organizationId));

    }


}
