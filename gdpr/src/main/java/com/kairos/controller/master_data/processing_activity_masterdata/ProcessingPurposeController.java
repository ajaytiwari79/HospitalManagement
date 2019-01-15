package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.ProcessingPurposeDTO;
import com.kairos.service.master_data.processing_activity_masterdata.ProcessingPurposeService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;

/*
 *
 *  created by bobby 20/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class ProcessingPurposeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingPurposeController.class);

    @Inject
    private ProcessingPurposeService processingPurposeService;


    @ApiOperation("add processing purpose")
    @PostMapping("/processing_purpose")
    public ResponseEntity<Object> createProcessingPurpose(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<ProcessingPurposeDTO> processingPurposes) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.createProcessingPurpose(countryId, processingPurposes.getRequestBody(), false));

    }


    @ApiOperation("get processing purpose by id")
    @GetMapping("/processing_purpose/{processingPurposeId}")
    public ResponseEntity<Object> getProcessingPurpose(@PathVariable Long countryId, @PathVariable Long processingPurposeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurpose(countryId, processingPurposeId));
    }


    @ApiOperation("get all processing purpose")
    @GetMapping("/processing_purpose")
    public ResponseEntity<Object> getAllProcessingPurpose(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getAllProcessingPurpose(countryId));
    }


    @ApiOperation("delete processing purpose by id")
    @DeleteMapping("/processing_purpose/{processingPurposeId}")
    public ResponseEntity<Object> deleteProcessingPurpose(@PathVariable Long countryId, @PathVariable Long processingPurposeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.deleteProcessingPurpose(countryId, processingPurposeId));

    }

    @ApiOperation("update processing purpose by id")
    @PutMapping("/processing_purpose/{processingPurposeId}")
    public ResponseEntity<Object> updateProcessingPurpose(@PathVariable Long countryId, @PathVariable Long processingPurposeId, @Valid @RequestBody ProcessingPurposeDTO processingPurpose) {
           return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.updateProcessingPurpose(countryId, processingPurposeId, processingPurpose));
    }


    @ApiOperation("update Suggested status of Processing Purposes")
    @PutMapping("/processing_purpose")
    public ResponseEntity<Object> updateSuggestedStatusOfProcessingPurposes(@PathVariable Long countryId, @RequestBody Set<Long> processingPurposeIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(processingPurposeIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Processing Purpose is Not Selected");
        } else if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.updateSuggestedStatusOfProcessingPurposeList(countryId, processingPurposeIds, suggestedDataStatus));
    }


}
