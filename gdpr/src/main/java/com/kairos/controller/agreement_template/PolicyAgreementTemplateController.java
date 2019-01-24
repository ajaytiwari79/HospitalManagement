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

    //TODO
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
    public ResponseEntity<ResponseDTO<AgreementTemplateSectionResponseDTO>> getAllAgreementSectionWithSubSectionsOfMasterAgreementTemplate(@PathVariable Long countryId, @PathVariable Long agreementTemplateId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.getAllSectionsAndSubSectionOfAgreementTemplateByAgreementTemplateIdAndReferenceId(countryId,false, agreementTemplateId));
    }


    @ApiOperation("delete master agreement Template By Id")
    @DeleteMapping(COUNTRY_URL+"/agreement_template/delete/{agreementTemplateId}")
    public ResponseEntity<ResponseDTO<Boolean>> deleteMasterAgreementTemplate(@PathVariable Long countryId, @PathVariable Long agreementTemplateId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.deletePolicyAgreementTemplate(countryId, false, agreementTemplateId));

    }


    @ApiOperation("update master agreement template basic details")
    @PutMapping(COUNTRY_URL+"/agreement_template/{agreementTemplateId}")
    public ResponseEntity<Object> updateMasterAgreementTemplate(@PathVariable Long countryId, @PathVariable Long agreementTemplateId, @Validated @RequestBody MasterAgreementTemplateDTO agreementTemplateDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.updatePolicyAgreementTemplateBasicDetails(countryId,false, agreementTemplateId, agreementTemplateDto));

    }


    @ApiOperation("get all master  policy agreement Template with basic details ,country level  ")
    @GetMapping(COUNTRY_URL+"/agreement_template")
    public ResponseEntity<ResponseDTO<List<PolicyAgreementTemplateResponseDTO>>> getAllMasterAgreementTemplate(@PathVariable Long countryId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.getAllAgreementTemplateByCountryId(countryId));

    }

    //TODO
    @ApiOperation("get All Master agreement Template linked with Clause , country level ")
    @GetMapping(COUNTRY_URL+"/agreement_template/clause/{clauseId}")
    public ResponseEntity<Object> getAllMasterAgreementTemplateByClauseId(@PathVariable Long countryId, @PathVariable BigInteger clauseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.getAllAgreementTemplateByReferenceIdAndClauseId(countryId, false,clauseId));

    }

    //TODO
    @ApiOperation("Replace Old Clause With New Version of Clause, country level ")
    @PutMapping(COUNTRY_URL+"/agreement_template/clause/version")
    public ResponseEntity<Object> updateMasterAgreementTemplateClauseWithNewVersion(@PathVariable Long countryId, @Valid @RequestBody AgreementTemplateClauseUpdateDTO agreementTemplateClauseUpdateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.updateAgreementTemplateClauseWithNewVersionByReferenceIdAndTemplateIds(countryId, false,agreementTemplateClauseUpdateDTO));

    }

    // Agreement template unit url

    @ApiOperation("save agreement template with basic detail , unit level ")
    @PostMapping(UNIT_URL+"/agreement_template")
    public ResponseEntity<ResponseDTO<AgreementTemplateDTO>> createPolicyAgreementTemplate(@PathVariable Long unitId, @Validated @RequestBody AgreementTemplateDTO agreementTemplateDto) {

        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.saveAgreementTemplate(unitId,true, agreementTemplateDto));
    }

    //TODO
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
    public ResponseEntity<ResponseDTO<Boolean>> deletePolicyAgreementTemplateByUnitIdAndId(@PathVariable Long unitId, @PathVariable Long agreementTemplateId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.deletePolicyAgreementTemplate(unitId, true, agreementTemplateId));

    }


    @ApiOperation("update  agreement template basic details , unit level ")
    @PutMapping(UNIT_URL+"/agreement_template/{agreementTemplateId}")
    public ResponseEntity<ResponseDTO<AgreementTemplateDTO>> updateAgreementTemplate(@PathVariable Long unitId, @PathVariable Long agreementTemplateId, @Validated @RequestBody AgreementTemplateDTO agreementTemplateDto) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.updatePolicyAgreementTemplateBasicDetails(unitId,true, agreementTemplateId, agreementTemplateDto));

    }

    @ApiOperation("get all policy agreement Template with basic details , unit  level  ")
    @GetMapping(UNIT_URL+"/agreement_template")
    public ResponseEntity<ResponseDTO<List<PolicyAgreementTemplateResponseDTO>>> getAllAgreementTemplateOfUnit(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.getAllAgreementTemplateByUnitId(unitId));

    }

    @ApiOperation("get all agreement sections and sub section of agreement template , unit level ")
    @GetMapping(UNIT_URL+ "/agreement_template/{agreementTemplateId}/section")
    public ResponseEntity<ResponseDTO<AgreementTemplateSectionResponseDTO>> getAllAgreementSectionWithSubSectionOfAgreementTemplate(@PathVariable Long unitId, @PathVariable Long agreementTemplateId) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, policyAgreementTemplateService.getAllSectionsAndSubSectionOfAgreementTemplateByAgreementTemplateIdAndReferenceId(unitId,true, agreementTemplateId));
    }

    //TODO
    @ApiOperation("get all agreement Template linked with clause , org level ")
    @GetMapping(UNIT_URL+"/agreement_template/clause/{clauseId}")
    public ResponseEntity<Object> getPolicyAgreementTemplateByClauseId(@PathVariable Long unitId, @PathVariable BigInteger clauseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.getAllAgreementTemplateByReferenceIdAndClauseId(unitId,true, clauseId));

    }

    //TODO
    @ApiOperation("Replace Old Clause With New Version of Clause , unit level")
    @PutMapping(UNIT_URL+"/agreement_template/clause/version")
    public ResponseEntity<Object> updateTemplateClauseWithNewVersion(@PathVariable Long unitId, @Valid @RequestBody AgreementTemplateClauseUpdateDTO agreementTemplateClauseUpdateDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.updateAgreementTemplateClauseWithNewVersionByReferenceIdAndTemplateIds(unitId, true,agreementTemplateClauseUpdateDTO));

    }

    //TODO
    @ApiOperation(value = "All Template Type type ")
    @GetMapping(UNIT_URL+"/template/all")
    public ResponseEntity<Object> getAllTemplateType(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, policyAgreementTemplateService.getAllTemplateType(unitId));
    }

}
