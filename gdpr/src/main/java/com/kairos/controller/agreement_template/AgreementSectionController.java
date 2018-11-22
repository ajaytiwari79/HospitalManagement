package com.kairos.controller.agreement_template;


import com.kairos.dto.gdpr.agreement_template.AgreementTemplateSectionDTO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementTemplateSectionResponseDTO;
import com.kairos.service.agreement_template.AgreementSectionService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.*;
/*
 *
 *  created by bobby 10/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class AgreementSectionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgreementSectionController.class);

    @Inject
    private AgreementSectionService agreementSectionService;


    @ApiOperation("save section of master Agreement template ,country level ")
    @PostMapping(COUNTRY_URL+ "/agreement_template/{templateId}/section")
    public ResponseEntity<ResponseDTO<AgreementTemplateSectionResponseDTO>> createMasterAgreementSection(@PathVariable Long countryId, @PathVariable BigInteger templateId, @Valid @RequestBody AgreementTemplateSectionDTO agreementTemplateSectionDTO) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, agreementSectionService.createAndUpdateAgreementSectionsAndClausesAndAddToAgreementTemplate(countryId,false, templateId, agreementTemplateSectionDTO));
    }

    @ApiOperation("deleted agreement section , country level")
    @DeleteMapping(COUNTRY_URL+"/agreement_template/{templateId}/section/delete/{id}")
    public ResponseEntity<Object> deleteMasterAgreementSection(@PathVariable Long countryId, @PathVariable BigInteger templateId, @PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.deleteAgreementSection(countryId, false,templateId, id));

    }

    @ApiOperation("deleted clause from section,clause")
    @DeleteMapping(COUNTRY_URL+ "/agreement_template/section/{sectionId}/clause/{clauseId}")
    public ResponseEntity<Object> deleteMasterClauseFromAgreementSection(@PathVariable BigInteger sectionId, @PathVariable BigInteger clauseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.removeClauseIdFromAgreementSection(sectionId, clauseId));

    }

    @ApiOperation("deleted agreement Sub Section  ")
    @DeleteMapping(COUNTRY_URL+ "/agreement_template/section/{sectionId}/sub_section/{subSectionId}")
    public ResponseEntity<Object> deleteMasterSubSection(@PathVariable BigInteger sectionId, @PathVariable BigInteger subSectionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.deleteAgreementSubSection(sectionId, subSectionId));

    }
    //unit url

    @ApiOperation("save section of Agreement template ,org level ")
    @PostMapping(UNIT_URL+ "/agreement_template/{templateId}/section")
    public ResponseEntity<ResponseDTO<AgreementTemplateSectionResponseDTO>> createAgreementSection(@PathVariable Long unitId, @PathVariable BigInteger templateId, @Valid @RequestBody AgreementTemplateSectionDTO agreementTemplateSectionDTO) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, agreementSectionService.createAndUpdateAgreementSectionsAndClausesAndAddToAgreementTemplate(unitId,true, templateId, agreementTemplateSectionDTO));
    }


    @ApiOperation("deleted agreement section , org level")
    @DeleteMapping(UNIT_URL+"/agreement_template/{templateId}/section/delete/{id}")
    public ResponseEntity<Object> deleteAgreementSection(@PathVariable Long unitId, @PathVariable BigInteger templateId, @PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.deleteAgreementSection(unitId, true,templateId, id));

    }

    @ApiOperation("deleted clause from section, org level")
    @DeleteMapping(UNIT_URL+ "/agreement_template/section/{sectionId}/clause/{clauseId}")
    public ResponseEntity<Object> deleteClauseFromAgreementSection(@PathVariable BigInteger sectionId, @PathVariable BigInteger clauseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.removeClauseIdFromAgreementSection(sectionId, clauseId));

    }

    @ApiOperation("deleted agreement Sub Section , org level")
    @DeleteMapping(UNIT_URL+ "/agreement_template/section/{sectionId}/sub_section/{subSectionId}")
    public ResponseEntity<Object> deleteSubSection(@PathVariable BigInteger sectionId, @PathVariable BigInteger subSectionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.deleteAgreementSubSection(sectionId, subSectionId));

    }

}
