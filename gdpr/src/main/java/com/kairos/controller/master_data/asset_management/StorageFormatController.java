package com.kairos.controller.master_data.asset_management;


import com.kairos.enums.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.StorageFormatDTO;
import com.kairos.service.master_data.asset_management.StorageFormatService;
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
 *  created by bobby 18/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class StorageFormatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageFormatController.class);

    @Inject
    private StorageFormatService storageFormatService;


    @ApiOperation("add StorageFormat")
    @PostMapping("/storage_format/add")
    public ResponseEntity<Object> createStorageFormat(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<StorageFormatDTO> storageFormat) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.createStorageFormat(countryId, storageFormat.getRequestBody()));

    }


    @ApiOperation("get StorageFormat by id")
    @GetMapping("/storage_format/{id}")
    public ResponseEntity<Object> getStorageFormat(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormat(countryId, id));
    }


    @ApiOperation("get all StorageFormat ")
    @GetMapping("/storage_format/all")
    public ResponseEntity<Object> getAllStorageFormat(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getAllStorageFormat(countryId));
    }


    @ApiOperation("get StorageFormat by name")
    @GetMapping("/storage_format/name")
    public ResponseEntity<Object> getStorageFormatByName(@PathVariable Long countryId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormatByName(countryId, name));

    }


    @ApiOperation("delete StorageFormat  by id")
    @DeleteMapping("/storage_format/delete/{id}")
    public ResponseEntity<Object> deleteStorageFormat(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.deleteStorageFormat(countryId, id));

    }


    @ApiOperation("update StorageFormat by id")
    @PutMapping("/storage_format/update/{id}")
    public ResponseEntity<Object> updateStorageFormat(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody StorageFormatDTO storageFormat) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.updateStorageFormat(countryId, id, storageFormat));

    }


    @ApiOperation("update Suggested status of Storage Format ")
    @PutMapping("/storage_format")
    public ResponseEntity<Object> updateSuggestedStatusOfStorageFormats(@PathVariable Long countryId, @RequestBody Set<BigInteger> storageFormatIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.updateSuggestedStatusOfStorageFormatList(countryId, storageFormatIds, suggestedDataStatus));
    }


}
