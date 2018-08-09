package com.kairos.controller.data_inventory.processing_activity;


import com.kairos.dto.metadata.ProcessingLegalBasisDTO;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.ProcessingLegalBasis;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class OrganizationProcessingLegalBasisController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationProcessingLegalBasisController.class);

    @Inject
    private OrganizationProcessingLegalBasisService legalBasisService;


    @ApiOperation("add ProcessingLegalBasis")
    @PostMapping("/legal_basis/add")
    public ResponseEntity<Object> createProcessingLegalBasis(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<ProcessingLegalBasisDTO> legalBases) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.createProcessingLegalBasis(unitId, legalBases.getRequestBody()));

    }


    @ApiOperation("get ProcessingLegalBasis by id")
    @GetMapping("/legal_basis/{id}")
    public ResponseEntity<Object> getProcessingLegalBasis(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getProcessingLegalBasis(unitId, id));
    }


    @ApiOperation("get all ProcessingLegalBasis ")
    @GetMapping("/legal_basis/all")
    public ResponseEntity<Object> getAllProcessingLegalBasis(@PathVariable Long unitId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getAllProcessingLegalBasis(unitId));
    }

    @ApiOperation("get ProcessingLegalBasis by name")
    @GetMapping("/legal_basis/name")
    public ResponseEntity<Object> getProcessingLegalBasisByName(@PathVariable Long unitId, @RequestParam String name) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getProcessingLegalBasisByName(unitId, name));

    }


    @ApiOperation("delete ProcessingLegalBasis  by id")
    @DeleteMapping("/legal_basis/delete/{id}")
    public ResponseEntity<Object> deleteProcessingLegalBasis(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.deleteProcessingLegalBasis(unitId, id));

    }


    @ApiOperation("update ProcessingLegalBasis by id")
    @PutMapping("/legal_basis/update/{id}")
    public ResponseEntity<Object> updateProcessingLegalBasis(@PathVariable Long unitId, @PathVariable BigInteger id, @Valid @RequestBody ProcessingLegalBasisDTO legalBasis) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.updateProcessingLegalBasis(unitId, id, legalBasis));

    }


}
