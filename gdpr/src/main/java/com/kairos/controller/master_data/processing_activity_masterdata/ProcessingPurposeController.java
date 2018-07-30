package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.persistance.model.master_data.default_proc_activity_setting.ProcessingPurpose;
import com.kairos.service.master_data.processing_activity_masterdata.ProcessingPurposeService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstant.UNIT_URL;

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
    public ResponseEntity<Object> createProcessingPurpose(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateListOfRequestBody<ProcessingPurpose> processingPurposes) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.createProcessingPurpose(countryId, organizationId, processingPurposes.getRequestBody()));

    }


    @ApiOperation("get processing purpose by id")
    @GetMapping("/processing_purpose/{id}")
    public ResponseEntity<Object> getProcessingPurpose(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurpose(countryId, organizationId, id));
    }


    @ApiOperation("get all processing purpose")
    @GetMapping("/processing_purpose/all")
    public ResponseEntity<Object> getAllProcessingPurpose(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getAllProcessingPurpose(countryId, organizationId));
    }

    @ApiOperation("get Processing purpose by name")
    @GetMapping("/processing_purpose/name")
    public ResponseEntity<Object> getProcessingPurposeByName(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurposeByName(countryId, organizationId, name));

    }


    @ApiOperation("delete processing purpose by id")
    @DeleteMapping("/processing_purpose/delete/{id}")
    public ResponseEntity<Object> deleteProcessingPurpose(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.deleteProcessingPurpose(countryId, organizationId, id));

    }

    @ApiOperation("get All processing purpose of Current organization and Parent Oeg which were not inherited by Organization")
    @GetMapping(UNIT_URL+"/inherit/processing_purpose")
    public ResponseEntity<Object> getAllProcessingPurposeOfOrganizationAndParentOrgWhichWereNotInherited(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getAllNotInheritedProcessingPurposesFromParentOrgAndUnitProcessingPurpose(countryId,organizationId,unitId));
    }



    @ApiOperation("update processing purpose by id")
    @PutMapping("/processing_purpose/update/{id}")
    public ResponseEntity<Object> updateProcessingPurpose(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody ProcessingPurpose processingPurpose) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.updateProcessingPurpose(countryId, organizationId, id, processingPurpose));
    }


    @ApiOperation("get processing purpose of unit by id")
    @GetMapping(UNIT_URL+"/processing_purpose/{id}")
    public ResponseEntity<Object> getProcessingPurposeOfUnitById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getProcessingPurpose(countryId, unitId, id));
    }


    @ApiOperation("get all processing purpose of unit")
    @GetMapping(UNIT_URL+"/processing_purpose/all")
    public ResponseEntity<Object> getAllProcessingPurposeOfUnit(@PathVariable Long countryId, @PathVariable Long unitId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, processingPurposeService.getAllProcessingPurpose(countryId, unitId));
    }

}
