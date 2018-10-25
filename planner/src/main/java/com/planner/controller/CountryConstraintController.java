package com.planner.controller;

import com.kairos.dto.planner.constarints.country.CountryConstraintDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.constraint.country.CountryConstraintService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_PARENT_ORGANIZATION_COUNTRY_CONSTRAINT_URL;

@RestController
@RequestMapping(API_PARENT_ORGANIZATION_COUNTRY_CONSTRAINT_URL)
public class CountryConstraintController {
    
    @Inject
    private CountryConstraintService countryConstraintService;
//=============================================================
    @PostMapping
    @ApiOperation("Create CountryConstraint")
    public ResponseEntity<Map<String, Object>> createCountryConstraint(@RequestBody CountryConstraintDTO countryConstraintDTO) {
        countryConstraintService.createCountryConstraint(countryConstraintDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @PostMapping (value = "/copy")
    @ApiOperation("Copy CountryConstraint")
    public ResponseEntity<Map<String, Object>> copyCountryConstraint(@RequestBody CountryConstraintDTO countryConstraintDTO) {
        countryConstraintService.copyCountryConstraint(countryConstraintDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @GetMapping
    @ApiOperation("Get CountryConstraint")
    public ResponseEntity<Map<String, Object>> getAllCountryConstraintByCountryId(@PathVariable Long countryId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,countryConstraintService.getAllCountryConstraintByCountryId(countryId));
    }


    @PatchMapping
    @ApiOperation("Update CountryConstraintration")
    public ResponseEntity<Map<String, Object>> updateCountryConstraint(@RequestBody CountryConstraintDTO countryConstraintDTO) {
        countryConstraintService.updateCountryConstraint(countryConstraintDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.ACCEPTED);
    }

    @DeleteMapping(value = "/{countryConstraintId}")
    @ApiOperation("Delete CountryConstraintration")
    public ResponseEntity<Map<String, Object>> deleteCountryConstraint(@PathVariable BigInteger countryConstraintId) {
        countryConstraintService.deleteCountryConstraint(countryConstraintId);
        return ResponseHandler.generateResponse("Success", HttpStatus.GONE);
    }

}
