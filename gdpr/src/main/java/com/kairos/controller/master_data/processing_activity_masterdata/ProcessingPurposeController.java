package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.enums.SuggestedDataStatus;
import com.kairos.gdpr.metadata.ProcessingPurposeDTO;
import com.kairos.service.master_data.processing_activity_masterdata.ProcessingPurposeService;
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
import java.util.Optional;
import java.util.Set;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;

/*
 *
 *  created by bobby 20/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class ProcessingPurposeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingPurposeController.class);

    @Inject
    private ProcessingPurposeService processingPurposeService;


    @ApiOperation("add processing purpose")
    @PostMapping("/processing_purpose/add")
    public ResponseEntity<Object> createProcessingPurpose(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<ProcessingPurposeDTO> processingPurposes) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.createProcessingPurpose(countryId, processingPurposes.getRequestBody()));

    }


    @ApiOperation("get processing purpose by id")
    @GetMapping("/processing_purpose/{id}")
    public ResponseEntity<Object> getProcessingPurpose(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurpose(countryId, id));
    }


    @ApiOperation("get all processing purpose")
    @GetMapping("/processing_purpose/all")
    public ResponseEntity<Object> getAllProcessingPurpose(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getAllProcessingPurpose(countryId));
    }

    @ApiOperation("get Processing purpose by name")
    @GetMapping("/processing_purpose/name")
    public ResponseEntity<Object> getProcessingPurposeByName(@PathVariable Long countryId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurposeByName(countryId, name));

    }


    @ApiOperation("delete processing purpose by id")
    @DeleteMapping("/processing_purpose/delete/{id}")
    public ResponseEntity<Object> deleteProcessingPurpose(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.deleteProcessingPurpose(countryId, id));

    }



    @ApiOperation("update processing purpose by id")
    @PutMapping("/processing_purpose/update/{id}")
    public ResponseEntity<Object> updateProcessingPurpose(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody ProcessingPurposeDTO processingPurpose) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.updateProcessingPurpose(countryId, id, processingPurpose));
    }


    @ApiOperation("update Suggested status of Processing Purposes")
    @PutMapping("/processing_purpose")
    public ResponseEntity<Object> updateSuggestedStatusOfProcessingPurposes(@PathVariable Long countryId, @RequestBody Set<BigInteger> processingPurposeIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true,processingPurposeService.updateSuggestedStatusOfProcessingPurposeList(countryId, processingPurposeIds, suggestedDataStatus));
    }


}
