package com.kairos.controller.data_inventory.asset;


import com.kairos.controller.master_data.asset_management.StorageFormatController;
import com.kairos.dto.gdpr.metadata.StorageFormatDTO;
import com.kairos.service.data_inventory.asset.OrganizationStorageFormatService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
class OrganizationStorageFormatController {


    private static final Logger LOGGER = LoggerFactory.getLogger(StorageFormatController.class);

    @Inject
    private OrganizationStorageFormatService storageFormatService;


    @ApiOperation("add StorageFormat")
    @PostMapping("/storage_format")
    public ResponseEntity<Object> createStorageFormat(@PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<StorageFormatDTO> storageFormat) {

        if (CollectionUtils.isEmpty(storageFormat.getRequestBody())) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.createStorageFormat(organizationId, storageFormat.getRequestBody()));

    }


    @ApiOperation("get StorageFormat by id")
    @GetMapping("/storage_format/{storageFormatId}")
    public ResponseEntity<Object> getStorageFormat(@PathVariable Long organizationId, @PathVariable Long storageFormatId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormat(organizationId, storageFormatId));
    }


    @ApiOperation("get all StorageFormat ")
    @GetMapping("/storage_format")
    public ResponseEntity<Object> getAllStorageFormat(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getAllStorageFormat(organizationId));
    }


    @ApiOperation("delete StorageFormat  by id")
    @DeleteMapping("/storage_format/{storageFormatId}")
    public ResponseEntity<Object> deleteStorageFormat(@PathVariable Long organizationId, @PathVariable Long storageFormatId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.deleteStorageFormat(organizationId, storageFormatId));

    }


    @ApiOperation("update StorageFormat by id")
    @PutMapping("/storage_format/{storageFormatId}")
    public ResponseEntity<Object> updateStorageFormat(@PathVariable Long organizationId, @PathVariable Long storageFormatId, @Valid @RequestBody StorageFormatDTO storageFormat) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.updateStorageFormat(organizationId, storageFormatId, storageFormat));

    }


    @ApiOperation("save Storage Format And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/storage_format/suggest")
    public ResponseEntity<Object> saveStorageFormatAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateRequestBodyList<StorageFormatDTO> storageFormatDTOs) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.saveAndSuggestStorageFormats(countryId, organizationId, storageFormatDTOs.getRequestBody()));

    }
}
