package com.kairos.controller.master_data;


import com.kairos.service.master_data.StorageFormatService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constant.ApiConstant.API_STORAGE_FORMAT_URL;


@RestController
@RequestMapping(API_STORAGE_FORMAT_URL)
@Api(API_STORAGE_FORMAT_URL)
public class StorageFormatController {


    @Inject
    private StorageFormatService storageFormatService;


    @ApiOperation("add StorageFormat")
    @PostMapping("/add")
    public ResponseEntity<Object> createStorageFormat(@RequestParam String storageFormat) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.createStorageFormat(storageFormat));

    }


    @ApiOperation("get StorageFormat by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getStorageFormatById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormatById(id));

    }


    @ApiOperation("get all StorageFormat ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllStorageFormat() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getAllStorageFormat());

    }


    @ApiOperation("delete StorageFormat  by id")
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> deleteStorageFormatById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.deleteStorageFormatById(id));

    }

    @ApiOperation("update StorageFormat by id")
    @PutMapping("/update/id/{id}")
    public ResponseEntity<Object> updateStorageFormat(@PathVariable BigInteger id, @RequestParam String storageFormat) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.updateStorageFormat(id, storageFormat));

    }


}
