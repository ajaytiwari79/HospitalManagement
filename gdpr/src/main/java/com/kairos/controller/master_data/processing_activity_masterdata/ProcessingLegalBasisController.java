package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.gdpr.metadata.ProcessingLegalBasisDTO;
import com.kairos.service.master_data.processing_activity_masterdata.ProcessingLegalBasisService;
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
 *  created by bobby 20/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class ProcessingLegalBasisController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingLegalBasisController.class);

    @Inject
    private ProcessingLegalBasisService legalBasisService;


    @ApiOperation("add ProcessingLegalBasis")
    @PostMapping("/legal_basis/add")
    public ResponseEntity<Object> createProcessingLegalBasis(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<ProcessingLegalBasisDTO> legalBases) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.createProcessingLegalBasis(countryId, legalBases.getRequestBody()));

    }


    @ApiOperation("get ProcessingLegalBasis by id")
    @GetMapping("/legal_basis/{id}")
    public ResponseEntity<Object> getProcessingLegalBasis(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getProcessingLegalBasis(countryId, id));
    }


    @ApiOperation("get all ProcessingLegalBasis ")
    @GetMapping("/legal_basis/all")
    public ResponseEntity<Object> getAllProcessingLegalBasis(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getAllProcessingLegalBasis(countryId));
    }

    @ApiOperation("get ProcessingLegalBasis by name")
    @GetMapping("/legal_basis/name")
    public ResponseEntity<Object> getProcessingLegalBasisByName(@PathVariable Long countryId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getProcessingLegalBasisByName(countryId, name));

    }


    @ApiOperation("delete ProcessingLegalBasis  by id")
    @DeleteMapping("/legal_basis/delete/{id}")
    public ResponseEntity<Object> deleteProcessingLegalBasis(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.deleteProcessingLegalBasis(countryId, id));

    }


    @ApiOperation("update ProcessingLegalBasis by id")
    @PutMapping("/legal_basis/update/{id}")
    public ResponseEntity<Object> updateProcessingLegalBasis(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody ProcessingLegalBasisDTO legalBasis) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.updateProcessingLegalBasis(countryId, id, legalBasis));
    }

    @ApiOperation("get All legal basis of Current organization and Parent Oeg which were not inherited by Organization")
    @GetMapping("/legal_basis")
    public ResponseEntity<Object> getAllLegalBasisOfOrganizationAndParentOrgWhichWereNotInherited(@PathVariable Long countryId,@PathVariable Long organizationId,@RequestParam Long parentOrgId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getAllNotInheritedLegalBasisFromParentOrgAndUnitProcessingLegalBasis(countryId,organizationId,parentOrgId));
    }


}
