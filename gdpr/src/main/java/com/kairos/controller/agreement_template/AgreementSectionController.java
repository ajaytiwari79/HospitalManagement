package com.kairos.controller.agreement_template;


import com.kairos.gdpr.master_data.AgreementSectionDTO;
import com.kairos.service.agreement_template.AgreementSectionService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
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


    @ApiOperation("add section to Agreement template ")
    @PostMapping(value = "/agreement_template/{templateId}/section")
    public ResponseEntity<Object> createAgreementSection(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger templateId,@Valid @RequestBody ValidateRequestBodyList<AgreementSectionDTO> agreementSection) {

        if (templateId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, " Agreement Template  id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.createAndUpdateAgreementSectionsAndClausesAndAddToAgreementTemplate(countryId, organizationId, templateId, agreementSection.getRequestBody()));

    }


    @ApiOperation("deleted agreement section by id")
    @DeleteMapping(value = "/agreement_template/{templateId}/section/delete/{id}")
    public ResponseEntity<Object> deleteAgreementSection(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger templateId, @PathVariable BigInteger id) {
        if (templateId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, " Agreement Template  id can't be null");
        }
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, " Agreement Template  id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.deleteAgreementSection(countryId, organizationId, templateId, id));

    }


    @ApiOperation("deleted clause from section ")
    @DeleteMapping(value = "/agreement_template/section/{sectionId}/clause/{clauseId}")
    public ResponseEntity<Object> deleteClauseFromAgreementSection(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger sectionId, @PathVariable BigInteger clauseId) {
        if (sectionId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Section  id can't be null");
        }
        if (clauseId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Clause  id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.removeClauseFromAgreementSection(countryId, organizationId, sectionId, clauseId));

    }

    @ApiOperation("deleted agreement  Sub Section  ")
    @DeleteMapping(value = "/agreement_template/section/{sectionId}/sub_section/{subSectionId}")
    public ResponseEntity<Object> deleteSubSectionFromAgreementSection(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger sectionId, @PathVariable BigInteger subSectionId) {
        if (sectionId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Section  id can't be null");
        }
        if (subSectionId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Agreement Sub Section  id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.deleteAgreementSubSection(countryId, organizationId, sectionId, subSectionId));

    }


    @ApiOperation("get agreement section by id")
    @GetMapping(value = "/agreement_template/section/{sectionId}")
    public ResponseEntity<Object> getAgreementSectionWithDataById(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger sectionId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (sectionId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, " Agreement Template  id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.getAgreementSectionWithDataById(countryId, sectionId));
    }


}
