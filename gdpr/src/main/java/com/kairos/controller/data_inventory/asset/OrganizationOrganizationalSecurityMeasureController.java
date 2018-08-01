package com.kairos.controller.data_inventory.asset;


import com.kairos.persistance.model.master_data.default_asset_setting.OrganizationalSecurityMeasure;
import com.kairos.service.data_inventory.asset.OrganizationOrganizationalSecurityMeasureService;
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

import static com.kairos.constants.ApiConstant.API_ORGANIZATION_URL_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_URL_UNIT_URL)
@Api(API_ORGANIZATION_URL_UNIT_URL)
public class OrganizationOrganizationalSecurityMeasureController {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationOrganizationalSecurityMeasureController.class);

    @Inject
    private OrganizationOrganizationalSecurityMeasureService organizationalSecurityMeasureService;


    @ApiOperation("add OrganizationalSecurityMeasure")
    @PostMapping("/organization_security/add")
    public ResponseEntity<Object> createOrganizationalSecurityMeasure(@PathVariable Long unitId, @Valid @RequestBody ValidateListOfRequestBody<OrganizationalSecurityMeasure> orgSecurityMeasures) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.createOrganizationalSecurityMeasure(unitId, orgSecurityMeasures.getRequestBody()));

    }


    @ApiOperation("get OrganizationalSecurityMeasure by id")
    @GetMapping("/organization_security/{id}")
    public ResponseEntity<Object> getOrganizationalSecurityMeasureById(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getOrganizationalSecurityMeasure(unitId, id));
    }


    @ApiOperation("get all OrganizationalSecurityMeasure ")
    @GetMapping("/organization_security/all")
    public ResponseEntity<Object> getAllOrganizationalSecurityMeasure(@PathVariable Long unitId) {
        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getAllOrganizationalSecurityMeasure(unitId));
    }


    @ApiOperation("get Organizational Security Measure by name")
    @GetMapping("/organization_security/name")
    public ResponseEntity<Object> getOrganizationalSecurityMeasureByName(@PathVariable Long unitId, @RequestParam String name) {

        if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getOrganizationalSecurityMeasureByName(unitId, name));

    }


    @ApiOperation("delete OrganizationalSecurityMeasure  by id")
    @DeleteMapping("/organization_security/delete/{id}")
    public ResponseEntity<Object> deleteOrganizationalSecurityMeasureById(@PathVariable Long unitId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.deleteOrganizationalSecurityMeasure(unitId, id));

    }

    @ApiOperation("update OrganizationalSecurityMeasure by id")
    @PutMapping("/organization_security/update/{id}")
    public ResponseEntity<Object> updateOrganizationalSecurityMeasure(@PathVariable Long unitId, @PathVariable BigInteger id, @Valid @RequestBody OrganizationalSecurityMeasure orgSecurityMeasure) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (unitId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.updateOrganizationalSecurityMeasure(unitId, id, orgSecurityMeasure));

    }


}
