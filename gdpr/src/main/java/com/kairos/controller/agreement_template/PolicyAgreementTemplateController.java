package com.kairos.controller.agreement_template;

import com.kairos.dto.gdpr.agreement_template.AgreementTemplateClauseUpdateDTO;
import com.kairos.dto.gdpr.agreement_template.PolicyAgreementTemplateDTO;
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
import javax.validation.Valid;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;
/*
 *
 *  created by bobby 10/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class PolicyAgreementTemplateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyAgreementTemplateController.class);

    @Inject
    private PolicyAgreementTemplateService policyAgreementTemplateService;


    @ApiOperation("create Agreement Template")
    @RequestMapping(value = "/agreement_template", method = RequestMethod.POST)
    public ResponseEntity<Object> createPolicyAgreementTemplate(@PathVariable Long countryId, @Validated @RequestBody PolicyAgreementTemplateDTO agreementTemplateDto) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.createBasicPolicyAgreementTemplate(countryId, agreementTemplateDto));

    }

    @ApiOperation("get All agreement sections and Subjection of Agreement template ")
    @GetMapping(value = "/agreement_template/{agreementTemplateId}/section")
    public ResponseEntity<Object> getAllAgreementSectionWithSubSectionsAndClausesOfAgreementTemplate(@PathVariable Long countryId, @PathVariable BigInteger agreementTemplateId) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.getAllAgreementSectionsAndSubSectionsOfAgreementTemplateByTemplateId(countryId, agreementTemplateId));
    }


    @ApiOperation("delete Policy agreement Template By Id")
    @DeleteMapping("/agreement_template/delete/{agreementTemplateId}")
    public ResponseEntity<Object> deletePolicyAgreementTemplateById(@PathVariable Long countryId, @PathVariable BigInteger agreementTemplateId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.deletePolicyAgreementTemplate(countryId, agreementTemplateId));

    }


    @ApiOperation("update Policy agreement Template Basic details")
    @PutMapping("/agreement_template/{agreementTemplateId}")
    public ResponseEntity<Object> deletePolicyAgreementTemplateById(@PathVariable Long countryId, @PathVariable BigInteger agreementTemplateId, @Validated @RequestBody PolicyAgreementTemplateDTO agreementTemplateDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.updatePolicyAgreementTemplateBasicDetails(countryId, agreementTemplateId, agreementTemplateDto));

    }


    @ApiOperation("get All policy agreement Template with sections and Clauses ")
    @GetMapping("/agreement_template")
    public ResponseEntity<Object> getPolicyAgreementTemplateWithData(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.getAllPolicyAgreementTemplate(countryId));

    }


    @ApiOperation("get All  agreement Template linked with Clauses ")
    @GetMapping("/agreement_template/clause/{clauseId}")
    public ResponseEntity<Object> getPolicatAgreementTemplateByClauseId(@PathVariable Long countryId,@PathVariable BigInteger clauseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.getAgreementTemplateListContainClause(countryId,clauseId));

    }

    @ApiOperation("Replace Old Clause With New Version of Clause")
    @PutMapping("/agreement_template/clause/version")
    public ResponseEntity<Object> upadateAgreementTemplateOldClauaseWithNewVersionOfClause(@PathVariable Long countryId,@Valid @RequestBody AgreementTemplateClauseUpdateDTO agreementTemplateClauseUpdateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.updateAgreementTemplateOldClauseWithNewVersionOfClause(countryId,agreementTemplateClauseUpdateDTO));

    }


}
