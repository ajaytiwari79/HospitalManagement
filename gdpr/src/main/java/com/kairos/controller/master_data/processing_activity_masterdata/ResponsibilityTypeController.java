package com.kairos.controller.master_data.processing_activity_masterdata;


import com.kairos.persistance.model.master_data.default_proc_activity_setting.ResponsibilityType;
import com.kairos.service.master_data.processing_activity_masterdata.ResponsibilityTypeService;
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
 *  created by bobby 20/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class ResponsibilityTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponsibilityTypeController.class);

    @Inject
    private ResponsibilityTypeService responsibilityTypeService;


    @ApiOperation("add ResponsibilityType  ")
    @PostMapping("/responsibility_type/add")
    public ResponseEntity<Object> createResponsibilityType(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateListOfRequestBody<ResponsibilityType> responsibilityTypes) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.createResponsibilityType(countryId, organizationId, responsibilityTypes.getRequestBody()));

    }


    @ApiOperation("get ResponsibilityType  by id")
    @GetMapping("/responsibility_type/{id}")
    public ResponseEntity<Object> getResponsibilityType(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getResponsibilityType(countryId, organizationId, id));
    }


    @ApiOperation("get all ResponsibilityType ")
    @GetMapping("/responsibility_type/all")
    public ResponseEntity<Object> getAllResponsibilityType(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getAllResponsibilityType(countryId, organizationId));

    }

    @ApiOperation("get ResponsibilityType by name")
    @GetMapping("/responsibility_type/name")
    public ResponseEntity<Object> getResponsibilityTypeByName(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getResponsibilityTypeByName(countryId, organizationId, name));

    }


    @ApiOperation("delete ResponsibilityType  by id")
    @DeleteMapping("/responsibility_type/delete/{id}")
    public ResponseEntity<Object> deleteResponsibilityType(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.deleteResponsibilityType(countryId, organizationId, id));

    }



    @ApiOperation("update ResponsibilityType  by id")
    @PutMapping("/responsibility_type/update/{id}")
    public ResponseEntity<Object> updateResponsibilityType(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody ResponsibilityType responsibilityType) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.updateResponsibilityType(countryId, organizationId, id, responsibilityType));

    }


    @ApiOperation("get All responsibility type of Current organization and Parent Oeg which were not inherited by Organization")
    @GetMapping(UNIT_URL+"/inherit/responsibility_type")
    public ResponseEntity<Object> getAllResponsibilityTypeOfOrganizationAndParentOrgWhichWereNotInherited(@PathVariable Long countryId,@PathVariable Long organizationId,@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getAllNotInheritedResponsibilityTypesFromParentOrgAndUnitResponsibilityType(countryId,organizationId,unitId));
    }



    @ApiOperation("get ResponsibilityType of unit by id")
    @GetMapping(UNIT_URL+"/responsibility_type/{id}")
    public ResponseEntity<Object> getResponsibilityTypeOfUnitById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getResponsibilityType(countryId, unitId, id));
    }


    @ApiOperation("get all ResponsibilityType of unit ")
    @GetMapping(UNIT_URL+"/responsibility_type/all")
    public ResponseEntity<Object> getAllResponsibilityTypeOfUnit(@PathVariable Long countryId, @PathVariable Long unitId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unitId can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, responsibilityTypeService.getAllResponsibilityType(countryId, unitId));

    }

}
