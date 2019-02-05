package com.kairos.controller.master_data.asset_management;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.StorageFormatDTO;
import com.kairos.service.master_data.asset_management.StorageFormatService;
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
import java.util.Optional;
import java.util.Set;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;


/*
 *
 *  created by bobby 18/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class StorageFormatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageFormatController.class);

    @Inject
    private StorageFormatService storageFormatService;


    @ApiOperation("add StorageFormat")
    @PostMapping("/storage_format")
    public ResponseEntity<Object> createStorageFormat(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<StorageFormatDTO> storageFormat) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.createStorageFormat(countryId, storageFormat.getRequestBody(), false));

    }


    @ApiOperation("get StorageFormat by id")
    @GetMapping("/storage_format/{storageFormatId}")
    public ResponseEntity<Object> getStorageFormat(@PathVariable Long countryId, @PathVariable Long storageFormatId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getStorageFormat(countryId, storageFormatId));
    }


    @ApiOperation("get all StorageFormat ")
    @GetMapping("/storage_format")
    public ResponseEntity<Object> getAllStorageFormat(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.getAllStorageFormat(countryId));
    }


    @ApiOperation("delete StorageFormat  by id")
    @DeleteMapping("/storage_format/{storageFormatId}")
    public ResponseEntity<Object> deleteStorageFormat(@PathVariable Long countryId, @PathVariable Long storageFormatId) {
      return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.deleteStorageFormat(countryId, storageFormatId));

    }


    @ApiOperation("update StorageFormat by id")
    @PutMapping("/storage_format/{storageFormatId}")
    public ResponseEntity<Object> updateStorageFormat(@PathVariable Long countryId, @PathVariable Long storageFormatId, @Valid @RequestBody StorageFormatDTO storageFormat) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.updateStorageFormat(countryId, storageFormatId, storageFormat));

    }


    @ApiOperation("update Suggested status of Storage Format ")
    @PutMapping("/storage_format")
    public ResponseEntity<Object> updateSuggestedStatusOfStorageFormats(@PathVariable Long countryId, @RequestBody Set<Long> storageFormatIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(storageFormatIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Storage Format is Not Selected");
        } else if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, storageFormatService.updateSuggestedStatusOfStorageFormatList(countryId, storageFormatIds, suggestedDataStatus));
    }


}
