package com.kairos.controller.data_inventory.asset;


import com.kairos.controller.master_data.asset_management.StorageFormatController;
import com.kairos.gdpr.metadata.StorageFormatDTO;
import com.kairos.service.data_inventory.asset.OrganizationStorageFormatService;
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
public class OrganizationStorageFormatController {


    private static final Logger LOGGER = LoggerFactory.getLogger(StorageFormatController.class);

    @Inject
    private OrganizationStorageFormatService storageFormatService;


    @ApiOperation("add StorageFormat")
    @PostMapping("/storage_format/add")
    public ResponseEntity<Object> createStorageFormat(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<StorageFormatDTO> storageFormat) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.createStorageFormat(unitId, storageFormat.getRequestBody()));

    }


    @ApiOperation("get StorageFormat by id")
    @GetMapping("/storage_format/{id}")
    public ResponseEntity<Object> getStorageFormat(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormat(unitId, id));
    }


    @ApiOperation("get all StorageFormat ")
    @GetMapping("/storage_format/all")
    public ResponseEntity<Object> getAllStorageFormat(@PathVariable Long unitId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getAllStorageFormat(unitId));
    }


    @ApiOperation("delete StorageFormat  by id")
    @DeleteMapping("/storage_format/delete/{id}")
    public ResponseEntity<Object> deleteStorageFormat(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.deleteStorageFormat(unitId, id));

    }


    @ApiOperation("update StorageFormat by id")
    @PutMapping("/storage_format/update/{id}")
    public ResponseEntity<Object> updateStorageFormat(@PathVariable Long unitId, @PathVariable BigInteger id, @Valid @RequestBody StorageFormatDTO storageFormat) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.updateStorageFormat(unitId, id, storageFormat));

    }


    @ApiOperation("save Storage Format And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/storage_format/suggest")
    public ResponseEntity<Object> saveStorageFormatAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<StorageFormatDTO> storageFormatDTOs) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.saveAndSuggestStorageFormats(countryId, unitId, storageFormatDTOs.getRequestBody()));

    }
}
