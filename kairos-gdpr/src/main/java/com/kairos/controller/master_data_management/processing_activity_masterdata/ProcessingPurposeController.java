package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ProcessingPurpose;
import com.kairos.service.master_data_management.processing_activity_masterdata.ProcessingPurposeService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.ApiConstant.API_PROCESSING_PURPOSE;
/*
 *
 *  created by bobby 20/5/2018
 * */


@RestController
@RequestMapping(API_PROCESSING_PURPOSE)
@Api(API_PROCESSING_PURPOSE)
public class ProcessingPurposeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingPurposeController.class);

    @Inject
    private ProcessingPurposeService processingPurposeService;


    @ApiOperation("add processing purpose")
    @PostMapping("/add")
    public ResponseEntity<Object> createProcessingPurpose(@PathVariable Long countryId,@Valid @RequestBody ValidateListOfRequestBody<ProcessingPurpose> processingPurposes) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.createProcessingPurpose(countryId, processingPurposes.getRequestBody()));

    }


    @ApiOperation("get processing purpose by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProcessingPurpose(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");

        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurpose(countryId, id));

    }


    @ApiOperation("get all processing purpose")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllProcessingPurpose() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getAllProcessingPurpose());

    }

    @ApiOperation("get Processing purpose by name")
    @GetMapping("/name")
    public ResponseEntity<Object> getProcessingPurposeByName(@PathVariable Long countryId, @RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurposeByName(countryId, name));

    }


    @ApiOperation("delete processing purpose by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteProcessingPurpose(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.deleteProcessingPurpose(id));

    }

    @ApiOperation("update processing purpose by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateProcessingPurpose(@PathVariable BigInteger id, @Valid @RequestBody ProcessingPurpose processingPurpose) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.updateProcessingPurpose(id, processingPurpose));

    }


}
