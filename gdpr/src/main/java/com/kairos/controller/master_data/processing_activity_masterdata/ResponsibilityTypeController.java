package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.ResponsibilityTypeDTO;
import com.kairos.service.master_data.processing_activity_masterdata.ResponsibilityTypeService;
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
import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_COUNTRY_URL;

/*
 *
 *  created by bobby 20/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_COUNTRY_URL)
@Api(API_ORGANIZATION_COUNTRY_URL)
public class ResponsibilityTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponsibilityTypeController.class);

    @Inject
    private ResponsibilityTypeService responsibilityTypeService;


    @ApiOperation("add ResponsibilityType  ")
    @PostMapping("/responsibility_type")
    public ResponseEntity<Object> createResponsibilityType(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<ResponsibilityTypeDTO> responsibilityTypes) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.createResponsibilityType(countryId, responsibilityTypes.getRequestBody()));

    }


    @ApiOperation("get ResponsibilityType  by id")
    @GetMapping("/responsibility_type/{responsibilityTypeId}")
    public ResponseEntity<Object> getResponsibilityType(@PathVariable Long countryId, @PathVariable Long responsibilityTypeId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getResponsibilityType(countryId, responsibilityTypeId));
    }


    @ApiOperation("get all ResponsibilityType ")
    @GetMapping("/responsibility_type")
    public ResponseEntity<Object> getAllResponsibilityType(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getAllResponsibilityType(countryId));

    }

    @ApiOperation("delete ResponsibilityType  by id")
    @DeleteMapping("/responsibility_type/{responsibilityTypeId}")
    public ResponseEntity<Object> deleteResponsibilityType(@PathVariable Long countryId, @PathVariable Long responsibilityTypeId) {
          return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.deleteResponsibilityType(countryId, responsibilityTypeId));

    }


    @ApiOperation("update ResponsibilityType  by id")
    @PutMapping("/responsibility_type/{responsibilityTypeId}")
    public ResponseEntity<Object> updateResponsibilityType(@PathVariable Long countryId, @PathVariable Long responsibilityTypeId, @Valid @RequestBody ResponsibilityTypeDTO responsibilityType) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.updateResponsibilityType(countryId, responsibilityTypeId, responsibilityType));

    }


    @ApiOperation("update Suggested status of Responsibility Types")
    @PutMapping("/responsibility_type")
    public ResponseEntity<Object> updateSuggestedStatusOfResponsibilityTypes(@PathVariable Long countryId, @RequestBody Set<Long> responsibilityTypeIds, @RequestParam(required = true) SuggestedDataStatus suggestedDataStatus) {
        if (CollectionUtils.isEmpty(responsibilityTypeIds)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Responsibility Type is Not Selected");
        } else if (!Optional.ofNullable(suggestedDataStatus).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Suggested Status in Empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.updateSuggestedStatusOfResponsibilityTypeList(countryId, responsibilityTypeIds, suggestedDataStatus));
    }


}
