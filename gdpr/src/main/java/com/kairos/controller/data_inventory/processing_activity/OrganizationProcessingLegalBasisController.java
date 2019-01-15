package com.kairos.controller.data_inventory.processing_activity;


import com.kairos.dto.gdpr.metadata.ProcessingLegalBasisDTO;
import com.kairos.service.data_inventory.processing_activity.OrganizationProcessingLegalBasisService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class OrganizationProcessingLegalBasisController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationProcessingLegalBasisController.class);

    @Inject
    private OrganizationProcessingLegalBasisService legalBasisService;


    @ApiOperation("add ProcessingLegalBasis")
    @PostMapping("/legal_basis")
    public ResponseEntity<Object> createProcessingLegalBasis(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<ProcessingLegalBasisDTO> legalBases) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.createProcessingLegalBasis(unitId, legalBases.getRequestBody()));
    }


    @ApiOperation("get ProcessingLegalBasis by id")
    @GetMapping("/legal_basis/{legalBasisId}")
    public ResponseEntity<Object> getProcessingLegalBasis(@PathVariable Long unitId, @PathVariable Long legalBasisId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getProcessingLegalBasis(unitId, legalBasisId));
    }


    @ApiOperation("get all ProcessingLegalBasis ")
    @GetMapping("/legal_basis")
    public ResponseEntity<Object> getAllProcessingLegalBasis(@PathVariable Long unitId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getAllProcessingLegalBasis(unitId));
    }

    @ApiOperation("delete ProcessingLegalBasis  by id")
    @DeleteMapping("/legal_basis/{legalBasisId}")
    public ResponseEntity<Object> deleteProcessingLegalBasis(@PathVariable Long unitId, @PathVariable BigInteger legalBasisId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.deleteProcessingLegalBasis(unitId, legalBasisId));

    }


    @ApiOperation("update ProcessingLegalBasis by id")
    @PutMapping("/legal_basis/{legalBasisId}")
    public ResponseEntity<Object> updateProcessingLegalBasis(@PathVariable Long unitId, @PathVariable Long legalBasisId, @Valid @RequestBody ProcessingLegalBasisDTO legalBasis) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.updateProcessingLegalBasis(unitId, legalBasisId, legalBasis));

    }


    @ApiOperation("save processing Legal BasisAnd Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/legal_basis/suggest")
    public ResponseEntity<Object> saveProcessingLegalBasisAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<ProcessingLegalBasisDTO> processingLegalBasisDTOs) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.saveAndSuggestProcessingLegalBasis(countryId, unitId, processingLegalBasisDTOs.getRequestBody()));

    }

}
