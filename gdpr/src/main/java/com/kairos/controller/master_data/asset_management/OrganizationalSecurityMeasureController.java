package com.kairos.controller.master_data.asset_management;


import com.kairos.dto.metadata.OrganizationalSecurityMeasureDTO;
import com.kairos.persistance.model.master_data.default_asset_setting.OrganizationalSecurityMeasure;
import com.kairos.service.master_data.asset_management.OrganizationalSecurityMeasureService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL;

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
    public ResponseEntity<Object> createOrganizationalSecurityMeasure(@PathVariable Long countryId, @Valid @RequestBody ValidateRequestBodyList<OrganizationalSecurityMeasureDTO> orgSecurityMeasures) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.createOrganizationalSecurityMeasure(countryId, orgSecurityMeasures.getRequestBody()));

    }


    @ApiOperation("get OrganizationalSecurityMeasure by id")
    @GetMapping("/organization_security/{id}")
    public ResponseEntity<Object> getOrganizationalSecurityMeasureById(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getOrganizationalSecurityMeasure(countryId, id));
    }


    @ApiOperation("get all OrganizationalSecurityMeasure ")
    @GetMapping("/organization_security/all")
    public ResponseEntity<Object> getAllOrganizationalSecurityMeasure(@PathVariable Long countryId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getAllOrganizationalSecurityMeasure(countryId));
    }


    @ApiOperation("get Organizational Security Measure by name")
    @GetMapping("/organization_security/name")
    public ResponseEntity<Object> getOrganizationalSecurityMeasureByName(@PathVariable Long countryId, @RequestParam String name) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getOrganizationalSecurityMeasureByName(countryId, name));

    }


    @ApiOperation("delete OrganizationalSecurityMeasure  by id")
    @DeleteMapping("/organization_security/delete/{id}")
    public ResponseEntity<Object> deleteOrganizationalSecurityMeasureById(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.deleteOrganizationalSecurityMeasure(countryId, id));

    }

    @ApiOperation("update OrganizationalSecurityMeasure by id")
    @PutMapping("/organization_security/update/{id}")
    public ResponseEntity<Object> updateOrganizationalSecurityMeasure(@PathVariable Long countryId, @PathVariable BigInteger id, @Valid @RequestBody OrganizationalSecurityMeasureDTO orgSecurityMeasure) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.updateOrganizationalSecurityMeasure(countryId, id, orgSecurityMeasure));
    }

    @ApiOperation("get All organization security   of Current organization and Parent Oeg which were not inherited by Organization")
    @GetMapping("/organization_security")
    public ResponseEntity<Object> getAllOrgSecurityMeasureOfOrganizationAndParentOrgWhichWereNotInherited(@PathVariable Long countryId, @PathVariable Long organizationId, @RequestParam Long parentOrgId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getAllNotInheritedFromParentOrgAndUnitOrgSecurityMeasure(countryId, parentOrgId, organizationId));
    }


}
