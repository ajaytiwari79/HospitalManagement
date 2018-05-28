package com.kairos.controller.master_data_management.asset_management;


import com.kairos.persistance.model.master_data_management.asset_management.OrganizationalSecurityMeasure;
import com.kairos.service.master_data_management.asset_management.OrganizationalSecurityMeasureService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.constant.ApiConstant.API_ORG_SEC_MEASURE_URL;
/*
 *
 *  created by bobby 17/5/2018
 * */


@RestController
@RequestMapping(API_ORG_SEC_MEASURE_URL)
@Api(API_ORG_SEC_MEASURE_URL)
@CrossOrigin
public class OrganizationalSecurityMeasureController {


    @Inject
    private OrganizationalSecurityMeasureService organizationalSecurityMeasureService;


    @ApiOperation("add OrganizationalSecurityMeasure")
    @PostMapping("/add")
    public ResponseEntity<Object> createOrganizationalSecurityMeasure(@PathVariable Long countryId, @RequestBody List<OrganizationalSecurityMeasure> orgSecurityMeasures) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.createOrganizationalSecurityMeasure(countryId, orgSecurityMeasures));

    }


    @ApiOperation("get OrganizationalSecurityMeasure by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrganizationalSecurityMeasureById(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id is null");

        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getOrganizationalSecurityMeasure(countryId, id));

    }


    @ApiOperation("get all OrganizationalSecurityMeasure ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllOrganizationalSecurityMeasure() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getAllOrganizationalSecurityMeasure());

    }


    @ApiOperation("get Organizational Security Measure by name")
    @GetMapping("/")
    public ResponseEntity<Object> getOrganizationalSecurityMeasureByName(@PathVariable Long countryId, @RequestParam String name) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getOrganizationalSecurityMeasureByName(countryId, name));

    }


    @ApiOperation("delete OrganizationalSecurityMeasure  by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteOrganizationalSecurityMeasureById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.deleteOrganizationalSecurityMeasure(id));

    }

    @ApiOperation("update OrganizationalSecurityMeasure by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateOrganizationalSecurityMeasure(@PathVariable BigInteger id, @RequestBody OrganizationalSecurityMeasure orgSecurityMeasure) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.updateOrganizationalSecurityMeasure(id, orgSecurityMeasure));

    }


}
