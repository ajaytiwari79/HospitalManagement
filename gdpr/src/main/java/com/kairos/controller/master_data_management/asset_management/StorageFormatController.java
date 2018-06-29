package com.kairos.controller.master_data_management.asset_management;


import com.kairos.persistance.model.master_data_management.asset_management.StorageFormat;
import com.kairos.service.master_data_management.asset_management.StorageFormatService;
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

import static com.kairos.constants.ApiConstant.API_STORAGE_FORMAT_URL;
/*
 *
 *  created by bobby 18/5/2018
 * */


@RestController
@RequestMapping(API_STORAGE_FORMAT_URL)
@Api(API_STORAGE_FORMAT_URL)
public class StorageFormatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageFormatController.class);

    @Inject
    private StorageFormatService storageFormatService;


    @ApiOperation("add StorageFormat")
    @PostMapping("/add")
    public ResponseEntity<Object> createStorageFormat(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateListOfRequestBody<StorageFormat> storageFormat) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.createStorageFormat(countryId, organizationId, storageFormat.getRequestBody()));

    }


    @ApiOperation("get StorageFormat by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getStorageFormat(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormat(countryId, organizationId, id));

    }


    @ApiOperation("get all StorageFormat ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllStorageFormat(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getAllStorageFormat(countryId, organizationId));

    }


    @ApiOperation("get StorageFormat by name")
    @GetMapping("/name")
    public ResponseEntity<Object> getStorageFormatByName(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormatByName(countryId, organizationId, name));

    }


    @ApiOperation("delete StorageFormat  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteStorageFormat(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.deleteStorageFormat(countryId, organizationId, id));

    }


    @ApiOperation("update StorageFormat by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateStorageFormat(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody StorageFormat storageFormat) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.updateStorageFormat(countryId, organizationId, id, storageFormat));

    }


}
