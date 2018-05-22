package com.kairos.controller.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ProcessingPurpose;
import com.kairos.service.master_data_management.processing_activity_masterdata.ProcessingPurposeService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.ApiConstant.API_PROCESSING_PURPOSE;

@RestController
@RequestMapping(API_PROCESSING_PURPOSE)
@Api(API_PROCESSING_PURPOSE)
@CrossOrigin
public class ProcessingPurposeController {


    @Inject
    private ProcessingPurposeService processingPurposeService;


    @ApiOperation("add pocessing purpose")
    @PostMapping("/add")
    public ResponseEntity<Object> createProcessingPurpose(@RequestBody List<ProcessingPurpose> processingPurposes) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.createProcessingPurpose(processingPurposes));

    }


    @ApiOperation("get pocessing purpose by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProcessingPurpose(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurpose(id));

    }


    @ApiOperation("get all pocessing purpose")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllProcessingPurpose() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getAllProcessingPurpose());

    }

    @ApiOperation("get ProcessingPurpose by name")
    @GetMapping("")
    public ResponseEntity<Object> getProcessingPurposeByName(@RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurposeByName(name));

    }



    @ApiOperation("delete pocessing purpose by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteProcessingPurpose(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.deleteProcessingPurpose(id));

    }

    @ApiOperation("update pocessing purpose by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateProcessingPurpose(@PathVariable BigInteger id,@RequestBody ProcessingPurpose   processingPurpose) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.updateProcessingPurpose(id,processingPurpose));

    }


}
