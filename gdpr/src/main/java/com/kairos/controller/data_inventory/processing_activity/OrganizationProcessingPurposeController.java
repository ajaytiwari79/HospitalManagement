package com.kairos.controller.data_inventory.processing_activity;


import com.kairos.gdpr.metadata.ProcessingPurposeDTO;
import com.kairos.service.data_inventory.processing_activity.OrganizationProcessingPurposeService;
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
import static com.kairos.constants.ApiConstant.COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class OrganizationProcessingPurposeController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationProcessingPurposeController.class);

    @Inject
    private OrganizationProcessingPurposeService processingPurposeService;


    @ApiOperation("add processing purpose")
    @PostMapping("/processing_purpose/add")
    public ResponseEntity<Object> createProcessingPurpose( @PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<ProcessingPurposeDTO> processingPurposes) {
       if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.createProcessingPurpose(unitId, processingPurposes.getRequestBody()));

    }


    @ApiOperation("get processing purpose by id")
    @GetMapping("/processing_purpose/{id}")
    public ResponseEntity<Object> getProcessingPurpose( @PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        else
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurpose(unitId, id));
    }


    @ApiOperation("get all processing purpose")
    @GetMapping("/processing_purpose/all")
    public ResponseEntity<Object> getAllProcessingPurpose( @PathVariable Long unitId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getAllProcessingPurpose(unitId));
    }

    @ApiOperation("get Processing purpose by name")
    @GetMapping("/processing_purpose/name")
    public ResponseEntity<Object> getProcessingPurposeByName( @PathVariable Long unitId, @RequestParam String name) {
       if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurposeByName(unitId, name));

    }


    @ApiOperation("delete processing purpose by id")
    @DeleteMapping("/processing_purpose/delete/{id}")
    public ResponseEntity<Object> deleteProcessingPurpose( @PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
       else
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.deleteProcessingPurpose(unitId, id));

    }

    @ApiOperation("update processing purpose by id")
    @PutMapping("/processing_purpose/update/{id}")
    public ResponseEntity<Object> updateProcessingPurpose( @PathVariable Long unitId, @PathVariable BigInteger id, @Valid @RequestBody ProcessingPurposeDTO processingPurpose) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
      else
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.updateProcessingPurpose(unitId, id, processingPurpose));
    }



    @ApiOperation("save processing purpose And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/processing_purpose")
    public ResponseEntity<Object> saveProcessingPurposeBasisAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<ProcessingPurposeDTO> processingPurposeDTOs) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.saveAndSuggestProcessingPurposes(countryId, unitId, processingPurposeDTOs.getRequestBody()));

    }
    
}
