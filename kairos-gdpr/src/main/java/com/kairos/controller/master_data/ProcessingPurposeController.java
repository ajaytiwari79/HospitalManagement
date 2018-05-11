package com.kairos.controller.master_data;


import com.kairos.persistance.model.master_data.ProcessingPurpose;
import com.kairos.service.master_data.ProcessingPurposeService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;

import static com.kairos.constant.ApiConstant.API_PROCESSING_PURPOSE;

@RestController
@RequestMapping(API_PROCESSING_PURPOSE)
@Api(API_PROCESSING_PURPOSE)
public class ProcessingPurposeController {


    @Inject
    private ProcessingPurposeService processingPurposeService;


    @ApiOperation("add pocessing purpose")
    @PostMapping("/add_purpose")
    public ResponseEntity<Object> createProcessingPurpose(@Valid @RequestBody ProcessingPurpose processingPurpose) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.createProcessingPurpose(processingPurpose));

    }


    @ApiOperation("get pocessing purpose by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getProcessingPurposeById(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurposeById(id));

    }


    @ApiOperation("get all pocessing purpose")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllProcessingPurpose() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getAllProcessingPurpose());

    }


    @ApiOperation("delete pocessing purpose by id")
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> deleteProcessingPurposeById(@PathVariable BigInteger id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.deleteProcessingPurposeById(id));

    }

    @ApiOperation("update pocessing purpose by id")
    @PutMapping("/update/id/{id}")
    public ResponseEntity<Object> updateProcessingPurpose(@PathVariable BigInteger id, @Valid @RequestBody ProcessingPurpose processingPurpose) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.updateProcessingPurpose(id, processingPurpose));

    }


}
