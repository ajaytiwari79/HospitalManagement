package com.kairos.controller.master_data.asset_management;


import com.kairos.persistance.model.master_data.default_asset_setting.StorageFormat;
import com.kairos.service.master_data.asset_management.StorageFormatService;
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
    @GetMapping("/storage_format/{id}")
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
    @GetMapping("/storage_format/all")
    public ResponseEntity<Object> getAllStorageFormat(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
       else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getAllStorageFormat(countryId, organizationId));
    }


    @ApiOperation("get StorageFormat by name")
    @GetMapping("/storage_format/name")
    public ResponseEntity<Object> getStorageFormatByName(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormatByName(countryId, organizationId, name));

    }


    @ApiOperation("delete StorageFormat  by id")
    @DeleteMapping("/storage_format/delete/{id}")
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


    @ApiOperation("get All storage format  of Current organization and Parent Oeg which were not inherited by Organization")
    @GetMapping("/storage_format")
    public ResponseEntity<Object> getAllStorageFormatMeasureOfOrganizationAndParentOrgWhichWereNotInherited(@PathVariable Long countryId,@PathVariable Long organizationId,@RequestParam Long parentOrgId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getAllNotInheritedStorageFormatFromParentOrgAndUnitStorageFormat(countryId,parentOrgId,organizationId));
    }


    @ApiOperation("update StorageFormat by id")
    @PutMapping("/storage_format/update/{id}")
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

    @ApiOperation("get StorageFormat of Unit by id")
    @GetMapping(UNIT_URL+"/storage_format/{id}")
    public ResponseEntity<Object> getStorageFormatOfUnitById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormat(countryId, unitId, id));
    }


    @ApiOperation("get all StorageFormat of Unit")
    @GetMapping(UNIT_URL+"/storage_format/all")
    public ResponseEntity<Object> getAllStorageFormatOfUnit(@PathVariable Long countryId, @PathVariable Long unitId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getAllStorageFormat(countryId, unitId));
    }


}
