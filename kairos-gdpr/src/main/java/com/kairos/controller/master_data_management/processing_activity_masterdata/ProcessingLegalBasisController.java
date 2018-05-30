package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ProcessingLegalBasis;
import com.kairos.service.master_data_management.processing_activity_masterdata.ProcessingLegalBasisService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.ApiConstant.API_PROCESSING_LEGAL_BASIS;
/*
 *
 *  created by bobby 20/5/2018
 * */


@RestController
@RequestMapping(API_PROCESSING_LEGAL_BASIS)
@Api(API_PROCESSING_LEGAL_BASIS)
@CrossOrigin
public class ProcessingLegalBasisController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingLegalBasisController.class);

    @Inject
    private ProcessingLegalBasisService legalBasisService;


    @ApiOperation("add ProcessingLegalBasis")
    @PostMapping("/add")
    public ResponseEntity<Object> createProcessingLegalBasis(@PathVariable Long countryId, @RequestBody List<ProcessingLegalBasis> legalBases) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.createProcessingLegalBasis(countryId, legalBases));

    }


    @ApiOperation("get ProcessingLegalBasis by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProcessingLegalBasis(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");

        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getProcessingLegalBasis(countryId, id));

    }


    @ApiOperation("get all ProcessingLegalBasis ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllProcessingLegalBasis() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getAllProcessingLegalBasis());

    }

    @ApiOperation("get ProcessingLegalBasis by name")
    @GetMapping("/name")
    public ResponseEntity<Object> getProcessingLegalBasisByName(@PathVariable Long countryId, @RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.getProcessingLegalBasisByName(countryId, name));

    }


    @ApiOperation("delete ProcessingLegalBasis  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteProcessingLegalBasis(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.deleteProcessingLegalBasis(id));

    }


    @ApiOperation("update ProcessingLegalBasis by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateProcessingLegalBasis(@PathVariable BigInteger id, @RequestBody ProcessingLegalBasis legalBasis) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, legalBasisService.updateProcessingLegalBasis(id, legalBasis));

    }


}
