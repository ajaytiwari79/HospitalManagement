package com.kairos.controller.agreement_template;

import com.kairos.dto.gdpr.agreement_template.AgreementTemplateClauseUpdateDTO;
import com.kairos.dto.gdpr.agreement_template.AgreementTemplateDTO;
import com.kairos.dto.gdpr.agreement_template.MasterAgreementTemplateDTO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementTemplateSectionResponseDTO;
import com.kairos.response.dto.policy_agreement.PolicyAgreementTemplateResponseDTO;
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
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.constants.ApiConstant.*;
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


    @ApiOperation("save master agreement template with basic detail")
    @PostMapping(COUNTRY_URL+"/agreement_template")
    public ResponseEntity<ResponseDTO<MasterAgreementTemplateDTO>> createMasterPolicyAgreementTemplate(@PathVariable Long countryId, @Validated @RequestBody MasterAgreementTemplateDTO agreementTemplateDto) {

        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.saveAgreementTemplate(countryId,false, agreementTemplateDto));
    }


    @ApiOperation("upload cover image of agreement template , country level")
    @PostMapping(COUNTRY_URL+"/agreement_template/{agreementTemplateId}/upload")
    public ResponseEntity<ResponseDTO<String>> uploadCoverPageLogoByCountryId(@PathVariable Long countryId, @PathVariable BigInteger agreementTemplateId, @RequestParam("file") MultipartFile file) {
        if (file.getSize() == 0) {
            return ResponseHandler.generateResponseDTO(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.uploadCoverPageLogo(countryId,false, agreementTemplateId, file));
    }

    @ApiOperation("get all agreement sections and sub section of master agreement template , country level ")
    @GetMapping(COUNTRY_URL+ "/agreement_template/{agreementTemplateId}/section")
    public ResponseEntity<ResponseDTO<AgreementTemplateSectionResponseDTO>> getAllAgreementSectionWithSubSectionsOfMasterAgreementTemplate(@PathVariable Long countryId, @PathVariable BigInteger agreementTemplateId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.getAllSectionsAndSubSectionOfAgreementTemplateByAgreementTemplateIdAndReferenceId(countryId,false, agreementTemplateId));
    }


    @ApiOperation("delete master agreement Template By Id")
    @DeleteMapping(COUNTRY_URL+"/agreement_template/delete/{agreementTemplateId}")
    public ResponseEntity<ResponseDTO<Boolean>> deleteMasterAgreementTemplate(@PathVariable Long countryId, @PathVariable BigInteger agreementTemplateId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.deletePolicyAgreementTemplate(countryId, false, agreementTemplateId));

    }


    @ApiOperation("update master agreement template basic details")
    @PutMapping(COUNTRY_URL+"/agreement_template/{agreementTemplateId}")
    public ResponseEntity<Object> updateMasterAgreementTemplate(@PathVariable Long countryId, @PathVariable BigInteger agreementTemplateId, @Validated @RequestBody MasterAgreementTemplateDTO agreementTemplateDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.updatePolicyAgreementTemplateBasicDetails(countryId,false, agreementTemplateId, agreementTemplateDto));

    }


    @ApiOperation("get all master  policy agreement Template with basic details ,country level  ")
    @GetMapping(COUNTRY_URL+"/agreement_template")
    public ResponseEntity<ResponseDTO<List<PolicyAgreementTemplateResponseDTO>>> getAllMasterAgreementTemplate(@PathVariable Long countryId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.getAllAgreementTemplateByCountryId(countryId));

    }


    @ApiOperation("get All  agreement Template linked with Clauses ")
    @GetMapping(COUNTRY_URL+"/agreement_template/clause/{clauseId}")
    public ResponseEntity<Object> getPolicyAgreementTemplateByClauseId(@PathVariable Long countryId, @PathVariable BigInteger clauseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.getAllAgreementTemplateByCountryIdAndClauseId(countryId, clauseId));

    }

    @ApiOperation("Replace Old Clause With New Version of Clause")
    @PutMapping(COUNTRY_URL+"/agreement_template/clause/version")
    public ResponseEntity<Object> updateAgreementTemplateOldClauseWithNewVersion(@PathVariable Long countryId, @Valid @RequestBody AgreementTemplateClauseUpdateDTO agreementTemplateClauseUpdateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.updateAgreementTemplateOldClauseWithNewVersionOfClause(countryId, agreementTemplateClauseUpdateDTO));

    }

    // Agreement template unit url

    @ApiOperation("save agreement template with basic detail , unit level ")
    @PostMapping(UNIT_URL+"/agreement_template")
    public ResponseEntity<ResponseDTO<AgreementTemplateDTO>> createPolicyAgreementTemplate(@PathVariable Long unitId, @Validated @RequestBody AgreementTemplateDTO agreementTemplateDto) {

        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.saveAgreementTemplate(unitId,true, agreementTemplateDto));
    }

    @ApiOperation("upload cover image of agreement template , unit level")
    @PostMapping(UNIT_URL+"/agreement_template/{agreementTemplateId}/upload")
    public ResponseEntity<ResponseDTO<String>> uploadCoverPageLogoByUnitId(@PathVariable Long unitId, @PathVariable BigInteger agreementTemplateId, @RequestParam("file") MultipartFile file) {
        if (file.getSize() == 0) {
            return ResponseHandler.generateResponseDTO(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.uploadCoverPageLogo(unitId,true, agreementTemplateId, file));
    }


    @ApiOperation("delete  agreement template by id ,unit level ")
    @DeleteMapping(UNIT_URL+"/agreement_template/delete/{agreementTemplateId}")
    public ResponseEntity<ResponseDTO<Boolean>> deletePolicyAgreementTemplateByUnitIdAndId(@PathVariable Long unitId, @PathVariable BigInteger agreementTemplateId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.deletePolicyAgreementTemplate(unitId, true, agreementTemplateId));

    }


    @ApiOperation("update  agreement template basic details , unit level ")
    @PutMapping(UNIT_URL+"/agreement_template/{agreementTemplateId}")
    public ResponseEntity<ResponseDTO<AgreementTemplateDTO>> updateAgreementTemplate(@PathVariable Long unitId, @PathVariable BigInteger agreementTemplateId, @Validated @RequestBody AgreementTemplateDTO agreementTemplateDto) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.updatePolicyAgreementTemplateBasicDetails(unitId,true, agreementTemplateId, agreementTemplateDto));

    }

    @ApiOperation("get all policy agreement Template with basic details , unit  level  ")
    @GetMapping(UNIT_URL+"/agreement_template")
    public ResponseEntity<ResponseDTO<List<PolicyAgreementTemplateResponseDTO>>> getAllAgreementTemplateOfUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.getAllAgreementTemplateByUnitId(unitId));

    }

    @ApiOperation("get all agreement sections and sub section of agreement template , unit level ")
    @GetMapping(UNIT_URL+ "/agreement_template/{agreementTemplateId}/section")
    public ResponseEntity<ResponseDTO<AgreementTemplateSectionResponseDTO>> getAllAgreementSectionWithSubSectionOfAgreementTemplate(@PathVariable Long unitId, @PathVariable BigInteger agreementTemplateId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.getAllSectionsAndSubSectionOfAgreementTemplateByAgreementTemplateIdAndReferenceId(unitId,true, agreementTemplateId));
    }


}
