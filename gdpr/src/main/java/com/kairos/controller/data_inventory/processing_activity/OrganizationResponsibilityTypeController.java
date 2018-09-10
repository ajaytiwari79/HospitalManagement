package com.kairos.controller.data_inventory.processing_activity;


import com.kairos.dto.gdpr.metadata.ResponsibilityTypeDTO;
import com.kairos.service.data_inventory.processing_activity.OrganizationResponsibilityTypeService;
import com.kairos.utils.ResponseHandler;
import com.kairos.utils.ValidateRequestBodyList;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL_UNIT_URL;
import static com.kairos.constants.ApiConstant.COUNTRY_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class OrganizationResponsibilityTypeController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationResponsibilityTypeController.class);

    @Inject
    private OrganizationResponsibilityTypeService responsibilityTypeService;


    @ApiOperation("add ResponsibilityType  ")
    @PostMapping("/responsibility_type/add")
    public ResponseEntity<Object> createResponsibilityType(@PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<ResponsibilityTypeDTO> responsibilityTypes) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.createResponsibilityType(unitId, responsibilityTypes.getRequestBody()));

    }


    @ApiOperation("get ResponsibilityType  by id")
    @GetMapping("/responsibility_type/{id}")
    public ResponseEntity<Object> getResponsibilityType(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getResponsibilityType(unitId, id));
    }


    @ApiOperation("get all ResponsibilityType ")
    @GetMapping("/responsibility_type/all")
    public ResponseEntity<Object> getAllResponsibilityType(@PathVariable Long unitId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getAllResponsibilityType(unitId));

    }

    @ApiOperation("get ResponsibilityType by name")
    @GetMapping("/responsibility_type/name")
    public ResponseEntity<Object> getResponsibilityTypeByName(@PathVariable Long unitId, @RequestParam String name) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getResponsibilityTypeByName(unitId, name));

    }


    @ApiOperation("delete ResponsibilityType  by id")
    @DeleteMapping("/responsibility_type/delete/{id}")
    public ResponseEntity<Object> deleteResponsibilityType(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.deleteResponsibilityType(unitId, id));

    }

    @ApiOperation("update ResponsibilityType  by id")
    @PutMapping("/responsibility_type/update/{id}")
    public ResponseEntity<Object> updateResponsibilityType(@PathVariable Long unitId, @PathVariable BigInteger id, @Valid @RequestBody ResponsibilityTypeDTO responsibilityType) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.updateResponsibilityType(unitId, id, responsibilityType));

    }


    @ApiOperation("save responsibility Type And Suggest To Country admin")
    @PostMapping(COUNTRY_URL + "/responsibility_type/suggest")
    public ResponseEntity<Object> saveResponsibilityTypeAndSuggestToCountryAdmin(@PathVariable Long countryId, @PathVariable Long unitId, @Valid @RequestBody ValidateRequestBodyList<ResponsibilityTypeDTO> responsibilityTypeDTOs) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.saveAndSuggestResponsibilityTypes(countryId, unitId, responsibilityTypeDTOs.getRequestBody()));

    }

}
