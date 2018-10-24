package com.planner.controller;

import com.kairos.dto.planner.solverconfig.country.CountrySolverConfigDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.solverconfiguration.CountrySolverConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_PARENT_ORGANIZATION_COUNTRY_SOLVER_CONFIG_URL;

@RestController
@RequestMapping(value =API_PARENT_ORGANIZATION_COUNTRY_SOLVER_CONFIG_URL)
public class CountrySolverConfigController {

    @Inject
    private CountrySolverConfigService  countrySolverConfigService;

    @PostMapping
    @ApiOperation("Create CountrySolverConfig")
    public ResponseEntity<Map<String, Object>> createCountrySolverConfig(@RequestBody CountrySolverConfigDTO countrySolverConfigDTO) {
        countrySolverConfigService.createCountrySolverConfig(countrySolverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @PostMapping (value = "/copy")
    @ApiOperation("Copy CountrySolverConfig")
    public ResponseEntity<Map<String, Object>> copyCountrySolverConfig(@RequestBody CountrySolverConfigDTO countrySolverConfigDTO) {
        countrySolverConfigService.copyCountrySolverConfig(countrySolverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @GetMapping(value = "/{solverConfigId}")
    @ApiOperation("Get CountrySolverConfig")
    public ResponseEntity<Map<String, Object>> getCountrySolverConfigById(@PathVariable BigInteger solverConfigId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,countrySolverConfigService.getCountrySolverConfigById(solverConfigId));
    }

    @GetMapping
    @ApiOperation("GetAll CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> getAllCountrySolverConfig() {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,countrySolverConfigService.getAllCountrySolverConfig());
    }

    /**
     * Always modification no object creation so,Patch
     * @param countrySolverConfigDTO
     * @return
     */
    @PatchMapping
    @ApiOperation("Update CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> updateCountrySolverConfig(@RequestBody CountrySolverConfigDTO countrySolverConfigDTO) {
        countrySolverConfigService.updateCountrySolverConfig(countrySolverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.ACCEPTED);
    }

    @DeleteMapping(value = "/{solverConfigId}")
    @ApiOperation("Delete CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> deleteCountrySolverConfig(@PathVariable BigInteger solverConfigId) {
        countrySolverConfigService.deleteCountrySolverConfig(solverConfigId);
        return ResponseHandler.generateResponse("Success", HttpStatus.GONE);
    }

    /**
     * Requires this data so that can send id of Sub OrganizationServicesId
     * @param countryId
     * @return
     */
    @GetMapping("/default_data")
    @ApiOperation("Get DefaultData")
    public ResponseEntity<Map<String, Object>> getDefaultData(@PathVariable Long countryId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,countrySolverConfigService.getDefaultData(countryId));
    }
}
