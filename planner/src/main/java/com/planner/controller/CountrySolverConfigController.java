package com.planner.controller;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.solverconfiguration.CountrySolverConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_PARENT_ORGANIZATION_COUNTRY_SOLVER_CONFIG_URL;

@RestController
@RequestMapping(value =API_PARENT_ORGANIZATION_COUNTRY_SOLVER_CONFIG_URL)
public class CountrySolverConfigController {

    @Inject
    private CountrySolverConfigService  countrySolverConfigService;

    @PostMapping
    @ApiOperation("Create CountrySolverConfig")
    public ResponseEntity<Map<String, Object>> createCountrySolverConfig(@RequestBody SolverConfigDTO solverConfigDTO) {
        countrySolverConfigService.createCountrySolverConfig(solverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @PostMapping (value = "/copy")
    @ApiOperation("Copy CountrySolverConfig")
    public ResponseEntity<Map<String, Object>> copyCountrySolverConfig(@RequestBody SolverConfigDTO solverConfigDTO) {
        countrySolverConfigService.copyCountrySolverConfig(solverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @GetMapping(value = "/{solverConfigId}")
    @ApiOperation("Get CountrySolverConfig")
    public ResponseEntity<Map<String, Object>> getCountrySolverConfig(@PathVariable String solverConfigId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,countrySolverConfigService.getCountrySolverConfig(solverConfigId));
    }

    @GetMapping
    @ApiOperation("GetAll CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> getAllCountrySolverConfig() {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,countrySolverConfigService.getAllCountrySolverConfig());
    }

    /**
     * Always modification no object creation so,Patch
     * @param solverConfigDTO
     * @return
     */
    @PatchMapping
    @ApiOperation("Update CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> updateCountrySolverConfig(@RequestBody SolverConfigDTO solverConfigDTO) {
        countrySolverConfigService.updateCountrySolverConfig(solverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.ACCEPTED);
    }

    @DeleteMapping
    @ApiOperation("Delete CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> deleteCountrySolverConfig(@RequestParam String solverConfigId) {
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
