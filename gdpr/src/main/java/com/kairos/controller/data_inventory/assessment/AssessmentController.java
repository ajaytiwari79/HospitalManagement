package com.kairos.controller.data_inventory.assessment;


import com.kairos.gdpr.data_inventory.AssessmentDTO;
import com.kairos.service.data_inventory.assessment.AssessmentService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;


@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL + COUNTRY_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL + COUNTRY_URL)
public class AssessmentController {


    @Inject
    private AssessmentService assessmentService;


    @ApiOperation(value = "Add assessment to Asset")
    @PostMapping("/assessment/asset/{assetId}")
    public ResponseEntity<Object> addAssessmentToAsset(@PathVariable Long unitId, @PathVariable Long countryId, @PathVariable BigInteger assetId, @RequestBody @Valid AssessmentDTO assessmentDTO) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can;t be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assessmentService.saveAssessmentForAsset(unitId, countryId, assetId, assessmentDTO));

    }


    @ApiOperation(value = "Add assessment to Processing Activity")
    @PostMapping("/assessment/processing_activity/{processingActivityId}")
    public ResponseEntity<Object> addAssessmentToProcessingActivity(@PathVariable Long unitId, @PathVariable Long countryId, @PathVariable BigInteger processingActivityId, @RequestBody @Valid AssessmentDTO assessmentDTO) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Organization id can;t be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, assessmentService.saveAssessmentForProcessingActivity(unitId, countryId, processingActivityId, assessmentDTO));

    }


}
