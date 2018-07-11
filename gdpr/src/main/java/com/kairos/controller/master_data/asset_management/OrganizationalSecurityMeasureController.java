package com.kairos.controller.master_data.asset_management;


import com.kairos.persistance.model.master_data.asset_management.OrganizationalSecurityMeasure;
import com.kairos.service.master_data.asset_management.OrganizationalSecurityMeasureService;
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
public class OrganizationalSecurityMeasureController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationalSecurityMeasureController.class);

    @Inject
    private OrganizationalSecurityMeasureService organizationalSecurityMeasureService;


    @ApiOperation("add OrganizationalSecurityMeasure")
    @PostMapping("/organization_security/add")
    public ResponseEntity<Object> createOrganizationalSecurityMeasure(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody ValidateListOfRequestBody<OrganizationalSecurityMeasure> orgSecurityMeasures) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.createOrganizationalSecurityMeasure(countryId, organizationId, orgSecurityMeasures.getRequestBody()));

    }


    @ApiOperation("get OrganizationalSecurityMeasure by id")
    @GetMapping("/organization_security/{id}")
    public ResponseEntity<Object> getOrganizationalSecurityMeasureById(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getOrganizationalSecurityMeasure(countryId, organizationId, id));
    }


    @ApiOperation("get all OrganizationalSecurityMeasure ")
    @GetMapping("/organization_security/all")
    public ResponseEntity<Object> getAllOrganizationalSecurityMeasure(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getAllOrganizationalSecurityMeasure(countryId, organizationId));
    }


    @ApiOperation("get Organizational Security Measure by name")
    @GetMapping("/organization_security/name")
    public ResponseEntity<Object> getOrganizationalSecurityMeasureByName(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getOrganizationalSecurityMeasureByName(countryId, organizationId, name));

    }


    @ApiOperation("delete OrganizationalSecurityMeasure  by id")
    @DeleteMapping("/organization_security/delete/{id}")
    public ResponseEntity<Object> deleteOrganizationalSecurityMeasureById(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.deleteOrganizationalSecurityMeasure(countryId, organizationId, id));

    }

    @ApiOperation("update OrganizationalSecurityMeasure by id")
    @PutMapping("/organization_security/update/{id}")
    public ResponseEntity<Object> updateOrganizationalSecurityMeasure(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id, @Valid @RequestBody OrganizationalSecurityMeasure orgSecurityMeasure) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.updateOrganizationalSecurityMeasure(countryId, organizationId, id, orgSecurityMeasure));

    }


    @ApiOperation("get OrganizationalSecurityMeasure of Unit by id")
    @GetMapping(UNIT_URL + "/organization_security/{id}")
    public ResponseEntity<Object> getOrganizationalSecurityMeasureOfUnitById(@PathVariable Long countryId, @PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unit id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getOrganizationalSecurityMeasure(countryId, unitId, id));
    }


    @ApiOperation("get all OrganizationalSecurityMeasure of Unit ")
    @GetMapping(UNIT_URL + "/organization_security/all")
    public ResponseEntity<Object> getAllOrganizationalSecurityMeasureOfUnit(@PathVariable Long countryId, @PathVariable Long unitId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "unit id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getAllOrganizationalSecurityMeasure(countryId, unitId));
    }


}
