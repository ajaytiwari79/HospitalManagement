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
    public ResponseEntity<Object> createAgreementSection(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger templateId, @RequestBody ValidateRequestBodyList<AgreementSectionDTO> agreementSection) {

        if (templateId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, " Agreement Template  id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.createAndUpdateAgreementSectionsAndClausesAndAddToAgreementTemplate(countryId, organizationId, templateId, agreementSection.getRequestBody()));

    }


    @ApiOperation("deleted agreement section by id")
    @DeleteMapping(value = "/agreement_template/{templateId}/section/delete/{id}")
    public ResponseEntity<Object> deleteAgreementSection(@PathVariable Long countryId, @PathVariable Long organizationId,@PathVariable BigInteger templateId, @PathVariable BigInteger id) {
        if (templateId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, " Agreement Template  id can't be null");
        }
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, " Agreement Template  id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.deleteAgreementSection(countryId,organizationId,templateId,id));

    }


    @ApiOperation("get agreement section by id")
    @GetMapping(value = "/agreement_section/section/{sectionId}")
    public ResponseEntity<Object> getAgreementSectionWithDataById(@PathVariable Long countryId, @PathVariable Long organizationId,@PathVariable BigInteger sectionId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        else if (sectionId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, " Agreement Template  id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.getAgreementSectionWithDataById(countryId, sectionId));
    }





}
