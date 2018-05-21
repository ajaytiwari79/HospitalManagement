package com.kairos.controller.asset_management;


import com.kairos.persistance.model.asset_management.StorageFormat;
import com.kairos.service.asset_management.StorageFormatService;
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


@RestController
@RequestMapping(API_STORAGE_FORMAT_URL)
@Api(API_STORAGE_FORMAT_URL)
public class StorageFormatController {


    @Inject
    private StorageFormatService storageFormatService;


    @ApiOperation("add StorageFormat")
    @PostMapping("/add")
    public ResponseEntity<Object> createStorageFormat(@RequestBody List<StorageFormat> storageFormat) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.createStorageFormat(storageFormat));

    }


    @ApiOperation("get StorageFormat by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getStorageFormat(@PathVariable BigInteger id) {
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
    public ResponseEntity<Object> deleteStorageFormat(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.deleteStorageFormatById(id));

    }

    @ApiOperation("update StorageFormat by id")
    @PutMapping("/update/id/{id}")
    public ResponseEntity<Object> updateStorageFormat(@PathVariable BigInteger id, @RequestBody StorageFormat storageFormat) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.updateStorageFormat(id, storageFormat));

    }


}
