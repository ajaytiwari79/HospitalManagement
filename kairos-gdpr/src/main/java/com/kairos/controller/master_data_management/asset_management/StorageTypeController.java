package com.kairos.controller.master_data_management.asset_management;


import com.kairos.persistance.model.master_data_management.asset_management.StorageType;
import com.kairos.service.master_data_management.asset_management.StorageTypeService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.ApiConstant.API_STORAGE_TYPE_URL;
/*
 *
 *  created by bobby 17/5/2018
 * */


@RestController
@RequestMapping(API_STORAGE_TYPE_URL)
@Api(API_STORAGE_TYPE_URL)
@CrossOrigin
public class StorageTypeController {
    @Inject
    private StorageTypeService storageTypeService;


    @ApiOperation("add StorageType")
    @PostMapping("/add")
    public ResponseEntity<Object> createStorageType(@PathVariable Long countryId, @RequestBody List<StorageType> storageTypes) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageTypeService.createStorageType(countryId, storageTypes));

    }


    @ApiOperation("get StorageFormat by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getStorageType(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");

        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, storageTypeService.getStorageType(countryId, id));

    }


    @ApiOperation("get all StorageFormat ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllStorageType() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageTypeService.getAllStorageType());

    }


    @ApiOperation("get StorageType by name")
    @GetMapping("/")
    public ResponseEntity<Object> getStorageTypeByName(@PathVariable Long countryId, @RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageTypeService.getStorageTypeByName(countryId, name));

    }


    @ApiOperation("delete StorageFormat  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteStorageType(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageTypeService.deleteStorageType(id));

    }

    @ApiOperation("update StorageFormat by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateStorageType(@PathVariable BigInteger id, @RequestBody StorageType storageType) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageTypeService.updateStorageType(id, storageType));

    }


}
