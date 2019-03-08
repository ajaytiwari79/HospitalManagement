package com.kairos.controller.data_inventory.processing_activity;


import com.kairos.dto.gdpr.metadata.ProcessingPurposeDTO;
import com.kairos.service.data_inventory.processing_activity.OrganizationProcessingPurposeService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
class OrganizationProcessingPurposeController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationProcessingPurposeController.class);

    @Inject
    private OrganizationProcessingPurposeService processingPurposeService;


    @ApiOperation("add processing purpose")
    @PostMapping("/processing_purpose")
    public ResponseEntity<Object> createProcessingPurpose(@PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<ProcessingPurposeDTO> processingPurposes) {
        if (CollectionUtils.isEmpty(processingPurposes.getRequestBody())) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.createProcessingPurpose(organizationId, processingPurposes.getRequestBody()));

    }


    @ApiOperation("get processing purpose by id")
    @GetMapping("/processing_purpose/{processingPurposeId}")
    public ResponseEntity<Object> getProcessingPurpose(@PathVariable Long organizationId, @PathVariable Long processingPurposeId) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurpose(organizationId, processingPurposeId));
    }


    @ApiOperation("get all processing purpose")
    @GetMapping("/processing_purpose")
    public ResponseEntity<Object> getAllProcessingPurpose(@PathVariable Long organizationId) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getAllProcessingPurpose(organizationId));
    }


    @ApiOperation("delete processing purpose by id")
    @DeleteMapping("/processing_purpose/{processingPurposeId}")
    public ResponseEntity<Object> deleteProcessingPurpose(@PathVariable Long organizationId, @PathVariable Long processingPurposeId) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.deleteProcessingPurpose(organizationId, processingPurposeId));

    }

    @ApiOperation("update processing purpose by id")
    @PutMapping("/processing_purpose/{processingPurposeId}")
    public ResponseEntity<Object> updateProcessingPurpose(@PathVariable Long organizationId, @PathVariable Long processingPurposeId, @Valid @RequestBody ProcessingPurposeDTO processingPurpose) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.updateProcessingPurpose(organizationId, processingPurposeId, processingPurpose));
    }


    @ApiOperation("save processing purpose And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/processing_purpose/suggest")
    public ResponseEntity<Object> saveProcessingPurposeBasisAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<ProcessingPurposeDTO> processingPurposeDTOs) {
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.saveAndSuggestProcessingPurposes(countryId, organizationId, processingPurposeDTOs.getRequestBody()));

    }

}
