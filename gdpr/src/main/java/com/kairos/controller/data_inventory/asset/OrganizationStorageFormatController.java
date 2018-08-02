package com.kairos.controller.data_inventory.asset;


import com.kairos.controller.master_data.asset_management.StorageFormatController;
import com.kairos.persistance.model.master_data.default_asset_setting.StorageFormat;
import com.kairos.service.data_inventory.asset.OrganizationStorageFormatService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class OrganizationStorageFormatController {


    private static final Logger LOGGER = LoggerFactory.getLogger(StorageFormatController.class);

    @Inject
    private OrganizationStorageFormatService storageFormatService;


    @ApiOperation("add StorageFormat")
    @PostMapping("/storage_format/add")
    public ResponseEntity<Object> createStorageFormat(@PathVariable Long unitId, @Valid @RequestBody ValidateListOfRequestBody<StorageFormat> storageFormat) {

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


    @ApiOperation("get StorageFormat by name")
    @GetMapping("/storage_format/name")
    public ResponseEntity<Object> getStorageFormatByName(@PathVariable Long unitId, @RequestParam String name) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormatByName(unitId, name));

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
    public ResponseEntity<Object> updateStorageFormat(@PathVariable Long unitId, @PathVariable BigInteger id, @Valid @RequestBody StorageFormat storageFormat) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.updateStorageFormat(unitId, id, storageFormat));

    }

}
