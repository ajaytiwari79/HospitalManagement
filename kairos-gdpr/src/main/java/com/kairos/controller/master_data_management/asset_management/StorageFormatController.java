package com.kairos.controller.master_data_management.asset_management;


import com.kairos.persistance.model.master_data_management.asset_management.StorageFormat;
import com.kairos.service.master_data_management.asset_management.StorageFormatService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
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
@CrossOrigin
public class StorageFormatController {


    @Inject
    private StorageFormatService storageFormatService;


    @ApiOperation("add StorageFormat")
    @PostMapping("/add")
    public ResponseEntity<Object> createStorageFormat(@RequestBody List<StorageFormat> storageFormat) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.createStorageFormat(storageFormat));

    }


    @ApiOperation("get StorageFormat by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getStorageFormat(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormat(id));

    }


    @ApiOperation("get all StorageFormat ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllStorageFormat() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getAllStorageFormat());

    }


    @ApiOperation("get StorageFormat by name")
    @GetMapping("/")
    public ResponseEntity<Object> getStorageFormatByName(@RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormatByName(name));

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
    public ResponseEntity<Object> updateStorageFormat(@PathVariable BigInteger id, @RequestBody StorageFormat storageFormat) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.updateStorageFormat(id, storageFormat));

    }


}
