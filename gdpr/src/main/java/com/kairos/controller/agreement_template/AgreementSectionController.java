package com.kairos.controller.agreement_template;


import com.kairos.dto.gdpr.agreement_template.AgreementSectionDTO;
import com.kairos.dto.gdpr.agreement_template.AgreementTemplateSectionDTO;
import com.kairos.dto.response.ResponseDTO;
import com.kairos.response.dto.policy_agreement.AgreementTemplateSectionResponseDTO;
import com.kairos.service.agreement_template.AgreementSectionService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class AgreementSectionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgreementSectionController.class);

    @Inject
    private AgreementSectionService agreementSectionService;


    @ApiOperation("add section to Agreement template ")
    @PostMapping(value = "/agreement_template/{templateId}/section")
    public ResponseEntity<ResponseDTO<AgreementTemplateSectionResponseDTO>> createAgreementSection(@PathVariable Long countryId, @PathVariable BigInteger templateId, @Valid @RequestBody AgreementTemplateSectionDTO agreementTemplateSectionDTO) {
        return ResponseHandler.generateResponseDTO(HttpStatus.OK, true, agreementSectionService.createAndUpdateAgreementSectionsAndClausesAndAddToAgreementTemplate(countryId, templateId, agreementTemplateSectionDTO));
    }

    @ApiOperation("deleted agreement section by id")
    @DeleteMapping(value = "/agreement_template/{templateId}/section/delete/{id}")
    public ResponseEntity<Object> deleteAgreementSection(@PathVariable Long countryId, @PathVariable BigInteger templateId, @PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.deleteAgreementSection(countryId, templateId, id));

    }


    @ApiOperation("deleted clause from section ")
    @DeleteMapping(value = "/agreement_template/section/{sectionId}/clause/{clauseId}")
    public ResponseEntity<Object> deleteClauseFromAgreementSection(@PathVariable Long countryId, @PathVariable BigInteger sectionId, @PathVariable BigInteger clauseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.removeClauseFromAgreementSection(countryId, sectionId, clauseId));

    }

    @ApiOperation("deleted agreement  Sub Section  ")
    @DeleteMapping(value = "/agreement_template/section/{sectionId}/sub_section/{subSectionId}")
    public ResponseEntity<Object> deleteSubSectionFromAgreementSection(@PathVariable Long countryId, @PathVariable BigInteger sectionId, @PathVariable BigInteger subSectionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.deleteAgreementSubSection(countryId, sectionId, subSectionId));

    }


    @ApiOperation("get agreement section by id")
    @GetMapping(value = "/agreement_template/section/{sectionId}")
    public ResponseEntity<Object> getAgreementSectionWithDataById(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger sectionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.getAgreementSectionWithDataById(countryId, sectionId));
    }


}
