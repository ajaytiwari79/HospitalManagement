package com.kairos.controller.data_inventory.processing_activity;


import com.kairos.dto.gdpr.metadata.ProcessingLegalBasisDTO;
import com.kairos.service.data_inventory.processing_activity.OrganizationProcessingLegalBasisService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
class OrganizationProcessingLegalBasisController {


    @Inject
    private OrganizationProcessingLegalBasisService legalBasisService;


    @ApiOperation("add ProcessingLegalBasis")
    @PostMapping("/legal_basis")
    public ResponseEntity<Object> createProcessingLegalBasis(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<ProcessingLegalBasisDTO> legalBases) {
        if (CollectionUtils.isEmpty(legalBases.getRequestBody())) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.createProcessingLegalBasis(unitId, legalBases.getRequestBody()));
    }


    @ApiOperation("get ProcessingLegalBasis by id")
    @GetMapping("/legal_basis/{legalBasisId}")
    public ResponseEntity<Object> getProcessingLegalBasis(@PathVariable Long unitId, @PathVariable Long legalBasisId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getProcessingLegalBasis(unitId, legalBasisId));
    }


    @ApiOperation("get all ProcessingLegalBasis ")
    @GetMapping("/legal_basis")
    public ResponseEntity<Object> getAllProcessingLegalBasis(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getAllProcessingLegalBasis(unitId));
    }

    @ApiOperation("delete ProcessingLegalBasis  by id")
    @DeleteMapping("/legal_basis/{legalBasisId}")
    public ResponseEntity<Object> deleteProcessingLegalBasis(@PathVariable Long unitId, @PathVariable Long legalBasisId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.deleteProcessingLegalBasis(unitId, legalBasisId));

    }


    @ApiOperation("update ProcessingLegalBasis by id")
    @PutMapping("/legal_basis/{legalBasisId}")
    public ResponseEntity<Object> updateProcessingLegalBasis(@PathVariable Long unitId, @PathVariable Long legalBasisId, @Valid @RequestBody ProcessingLegalBasisDTO legalBasis) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.updateProcessingLegalBasis(unitId, legalBasisId, legalBasis));

    }


    @ApiOperation("save processing Legal BasisAnd Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/legal_basis/suggest")
    public ResponseEntity<Object> saveProcessingLegalBasisAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<ProcessingLegalBasisDTO> processingLegalBasisDTOs) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.saveAndSuggestProcessingLegalBasis(countryId, unitId, processingLegalBasisDTOs.getRequestBody()));

    }

}
