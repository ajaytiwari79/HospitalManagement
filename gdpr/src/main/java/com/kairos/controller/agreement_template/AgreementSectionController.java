package com.kairos.controller.agreement_template;


import com.kairos.dto.master_data.AgreementSectionDTO;
import com.kairos.service.agreement_template.AgreementSectionService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.math.BigInteger;
import java.util.Set;

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
    public ResponseEntity<Object> createAgreementSection(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger templateId, @RequestBody ValidateListOfRequestBody<AgreementSectionDTO> agreementSection) {

        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        if (templateId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, " Agreement Template  id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.createUpdateAgreementSectionsAndClausesAndAddToAgreementTemplate(countryId, organizationId, templateId, agreementSection.getRequestBody()));

    }


    @DeleteMapping(value = "/agreement_template/section/delete/{id}")
    public ResponseEntity<Object> deleteAgreementSection(@PathVariable BigInteger id) {
        if (id != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.deleteAgreementSection(id));
        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "request id Cannot be null");

    }


    @GetMapping(value = "/agreement_section/section/{id}")
    public ResponseEntity<Object> getAgreementSectionWithDataById(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.getAgreementSectionWithDataById(countryId, id));
        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "request id Cannot be null");
    }


    @GetMapping(value = "/agreement_section/all")
    public ResponseEntity<Object> getAllAgreementSection(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.getAllAgreementSection(countryId));

    }


    @PostMapping(value = "/agreement_section/list")
    public ResponseEntity<Object> getAllAgreementSectionList(@PathVariable Long countryId, @RequestBody Set<BigInteger> ids) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, agreementSectionService.getAgreementSectionWithDataList(countryId, ids));

    }

}
