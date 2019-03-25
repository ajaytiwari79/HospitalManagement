package com.kairos.controller.data_inventory.processing_activity;


import com.kairos.dto.gdpr.metadata.ResponsibilityTypeDTO;
import com.kairos.service.data_inventory.processing_activity.OrganizationResponsibilityTypeService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
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
class OrganizationResponsibilityTypeController {

    @Inject
    private OrganizationResponsibilityTypeService responsibilityTypeService;


    @ApiOperation("add ResponsibilityType  ")
    @PostMapping("/responsibility_type")
    public ResponseEntity<Object> createResponsibilityType(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<ResponsibilityTypeDTO> responsibilityTypes) {
        if (CollectionUtils.isEmpty(responsibilityTypes.getRequestBody())) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, null);
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.createResponsibilityType(unitId, responsibilityTypes.getRequestBody()));

    }


    @ApiOperation("get ResponsibilityType  by id")
    @GetMapping("/responsibility_type/{responsibilityTypeId}")
    public ResponseEntity<Object> getResponsibilityType(@PathVariable Long unitId, @PathVariable Long responsibilityTypeId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getResponsibilityType(unitId, responsibilityTypeId));
    }


    @ApiOperation("get all ResponsibilityType ")
    @GetMapping("/responsibility_type")
    public ResponseEntity<Object> getAllResponsibilityType(@PathVariable Long unitId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getAllResponsibilityType(unitId));

    }

    @ApiOperation("delete ResponsibilityType  by id")
    @DeleteMapping("/responsibility_type/{responsibilityTypeId}")
    public ResponseEntity<Object> deleteResponsibilityType(@PathVariable Long unitId, @PathVariable Long responsibilityTypeId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.deleteResponsibilityType(unitId, responsibilityTypeId));

    }

    @ApiOperation("update ResponsibilityType  by id")
    @PutMapping("/responsibility_type/{responsibilityTypeId}")
    public ResponseEntity<Object> updateResponsibilityType(@PathVariable Long unitId, @PathVariable Long responsibilityTypeId, @Valid @RequestBody ResponsibilityTypeDTO responsibilityType) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.updateResponsibilityType(unitId, responsibilityTypeId, responsibilityType));

    }


    @ApiOperation("save responsibility Type And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/responsibility_type/suggest")
    public ResponseEntity<Object> saveResponsibilityTypeAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<ResponsibilityTypeDTO> responsibilityTypeDTOs) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id does not exist");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.saveAndSuggestResponsibilityTypes(countryId, unitId, responsibilityTypeDTOs.getRequestBody()));

    }

}
