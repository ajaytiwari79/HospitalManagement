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
import java.util.List;

import static com.kairos.constant.ApiConstant.API_STORAGE_FORMAT_URL;
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
    public ResponseEntity<Object> createStorageFormat(@PathVariable Long countryId,@Valid @RequestBody ValidateListOfRequestBody<StorageFormat> storageFormat) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.createStorageFormat(countryId, storageFormat.getRequestBody()));

    }


    @ApiOperation("get StorageFormat by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getStorageFormat(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");

        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormat(countryId, id));

    }


    @ApiOperation("get all StorageFormat ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllStorageFormat() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getAllStorageFormat());

    }


    @ApiOperation("get StorageFormat by name")
    @GetMapping("/name")
    public ResponseEntity<Object> getStorageFormatByName(@PathVariable Long countryId, @RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormatByName(countryId, name));

    }


    @ApiOperation("delete StorageFormat  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteStorageFormat(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.deleteStorageFormat(id));

    }


    @ApiOperation("update StorageFormat by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateStorageFormat(@PathVariable BigInteger id,  @Valid @RequestBody StorageFormat storageFormat) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.updateStorageFormat(id, storageFormat));

    }


}
