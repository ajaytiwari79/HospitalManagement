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
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_PROCESSING_PURPOSE;
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
    public ResponseEntity<Object> createProcessingPurpose(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateListOfRequestBody<ProcessingPurpose> processingPurposes) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "basic_details id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.createProcessingPurpose(countryId, organizationId, processingPurposes.getRequestBody()));

    }


    @ApiOperation("get processing purpose by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProcessingPurpose(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "basic_details id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurpose(countryId, organizationId, id));

    }


    @ApiOperation("get all processing purpose")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllProcessingPurpose(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "basic_details id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getAllProcessingPurpose(countryId, organizationId));

    }

    @ApiOperation("get Processing purpose by name")
    @GetMapping("/name")
    public ResponseEntity<Object> getProcessingPurposeByName(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "basic_details id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurposeByName(countryId, organizationId, name));

    }


    @ApiOperation("delete processing purpose by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteProcessingPurpose(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "basic_details id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.deleteProcessingPurpose(countryId, organizationId, id));

    }

    @ApiOperation("update processing purpose by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateProcessingPurpose(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody ProcessingPurpose processingPurpose) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "basic_details id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.updateProcessingPurpose(countryId, organizationId, id, processingPurpose));

    }


}
