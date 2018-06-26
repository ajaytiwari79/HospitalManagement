package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ProcessingLegalBasis;
import com.kairos.service.master_data_management.processing_activity_masterdata.ProcessingLegalBasisService;
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
import javax.validation.Valid;
import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_PROCESSING_LEGAL_BASIS;
/*
 *
 *  created by bobby 20/5/2018
 * */


@RestController
@RequestMapping(API_PROCESSING_LEGAL_BASIS)
@Api(API_PROCESSING_LEGAL_BASIS)
public class ProcessingLegalBasisController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingLegalBasisController.class);

    @Inject
    private ProcessingLegalBasisService legalBasisService;


    @ApiOperation("add ProcessingLegalBasis")
    @PostMapping("/add")
    public ResponseEntity<Object> createProcessingLegalBasis(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateListOfRequestBody<ProcessingLegalBasis> legalBases) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.createProcessingLegalBasis(countryId, organizationId, legalBases.getRequestBody()));

    }


    @ApiOperation("get ProcessingLegalBasis by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProcessingLegalBasis(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getProcessingLegalBasis(countryId, organizationId, id));

    }


    @ApiOperation("get all ProcessingLegalBasis ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllProcessingLegalBasis(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getAllProcessingLegalBasis(countryId, organizationId));

    }

    @ApiOperation("get ProcessingLegalBasis by name")
    @GetMapping("/name")
    public ResponseEntity<Object> getProcessingLegalBasisByName(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getProcessingLegalBasisByName(countryId, organizationId, name));

    }


    @ApiOperation("delete ProcessingLegalBasis  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteProcessingLegalBasis(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.deleteProcessingLegalBasis(countryId, organizationId, id));

    }


    @ApiOperation("update ProcessingLegalBasis by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateProcessingLegalBasis(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody ProcessingLegalBasis legalBasis) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.updateProcessingLegalBasis(countryId, organizationId, id, legalBasis));

    }


}
