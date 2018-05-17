package com.kairos.controller.master_data;


import com.kairos.service.master_data.OrganizationalSecurityMeasureService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;

import static com.kairos.constant.ApiConstant.API_ORG_SEC_MEASURE_URL;


@RestController
@RequestMapping(API_ORG_SEC_MEASURE_URL)
@Api(API_ORG_SEC_MEASURE_URL)
public class OrganizationalSecurityMeasureController {


    @Inject
    private OrganizationalSecurityMeasureService organizationalSecurityMeasureService;


    @ApiOperation("add OrganizationalSecurityMeasure")
    @PostMapping("/add")
    public ResponseEntity<Object> createOrganizationalSecurityMeasure(@RequestParam String orgSecurityMeasure) {

        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.createOrganizationalSecurityMeasure(orgSecurityMeasure));

    }


    @ApiOperation("get OrganizationalSecurityMeasure by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getOrganizationalSecurityMeasureById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getOrganizationalSecurityMeasureById(id));

    }


    @ApiOperation("get all OrganizationalSecurityMeasure ")
    @GetMapping("/all")
    public ResponseEntity<Object> getAllOrganizationalSecurityMeasure() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.getAllOrganizationalSecurityMeasure());

    }


    @ApiOperation("delete OrganizationalSecurityMeasure  by id")
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> deleteOrganizationalSecurityMeasureById(@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.deleteOrganizationalSecurityMeasureById(id));

    }

    @ApiOperation("update OrganizationalSecurityMeasure by id")
    @PutMapping("/update/id/{id}")
    public ResponseEntity<Object> updateOrganizationalSecurityMeasure(@PathVariable BigInteger id, @RequestParam String orgSecurityMeasure) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id is null");
        }

        return ResponseHandler.generateResponse(HttpStatus.OK, true, organizationalSecurityMeasureService.updateOrganizationalSecurityMeasure(id, orgSecurityMeasure));

    }


}
