package com.kairos.controller.master_data.asset_management;


import com.kairos.persistance.model.master_data.default_asset_setting.HostingType;
import com.kairos.service.master_data.asset_management.HostingTypeService;
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
 *  created by bobby 17/5/2018
 * */


@RestController
@RequestMapping(API_ORGANIZATION_URL)
@Api(API_ORGANIZATION_URL)
public class HostingTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostingTypeController.class);

    @Inject
    private HostingTypeService hostingTypeService;


    @ApiOperation("add HostingType")
    @PostMapping("/hosting_type/add")
    public ResponseEntity<Object> createHostingType(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateListOfRequestBody<HostingType> hostingTypes) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.createHostingType(countryId, organizationId, hostingTypes.getRequestBody()));

    }


    @ApiOperation("get HostingType by id")
    @GetMapping("/hosting_type/{id}")
    public ResponseEntity<Object> getHostingType(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getHostingType(countryId, organizationId, id));

    }


    @ApiOperation("get all HostingType ")
    @GetMapping("/hosting_type/all")
    public ResponseEntity<Object> getAllHostingType(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getAllHostingType(countryId, organizationId));
    }


    @ApiOperation("get HostingType by name")
    @GetMapping("/hosting_type/name")
    public ResponseEntity<Object> getHostingTypeByName(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getHostingTypeByName(countryId, organizationId, name));

    }


    @ApiOperation("delete HostingType  by id")
    @DeleteMapping("/hosting_type/delete/{id}")
    public ResponseEntity<Object> deleteHostingType(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.deleteHostingType(countryId, organizationId, id));

    }

    @ApiOperation("update HostingType by id")
    @PutMapping("/hosting_type/update/{id}")
    public ResponseEntity<Object> updateHostingType(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody HostingType hostingtype) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.updateHostingType(countryId, organizationId, id, hostingtype));

    }

    @ApiOperation("get All hosting type  of Current organization and Parent Oeg which were not inherited by Organization")
    @GetMapping("/hosting_type")
    public ResponseEntity<Object> getAllHostingTypeOfOrganizationAndParentOrgWhichWereNotInherited(@PathVariable Long countryId,@PathVariable Long organizationId,@RequestParam Long parentOrgId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getAllNotInheritedHostingTypeFromParentOrgAndUnitHostingType(countryId,parentOrgId,organizationId));
    }


    @ApiOperation("get HostingType of unit by id")
    @GetMapping(UNIT_URL+"/hosting_type/{id}")
    public ResponseEntity<Object> getHostingTypeOfUnitById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unit id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getHostingType(countryId, unitId, id));

    }


    @ApiOperation("get all HostingType of unit ")
    @GetMapping(UNIT_URL+"/hosting_type/all")
    public ResponseEntity<Object> getAllHostingTypeOfUnit(@PathVariable Long countryId, @PathVariable Long unitId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unit id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, hostingTypeService.getAllHostingType(countryId, unitId));
    }

}
