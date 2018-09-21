package com.planner.controller;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.solverconfiguration.CountrySolverConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping
public class CountrySolverConfigController {

    @Inject
    private CountrySolverConfigService  countrySolverConfigService;

    @PostMapping (value = "/create")
    @ApiOperation("Create CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> createCountrySolverConfig(@RequestBody SolverConfigDTO solverConfigDTO) {
        countrySolverConfigService.createCountrySolverConfig(solverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @GetMapping(value = "/getOne")
    @ApiOperation("Get CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> getCountrySolverConfig(@RequestParam String solverConfigId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,countrySolverConfigService.getCountrySolverConfig(solverConfigId));
    }

    @GetMapping(value = "/getAll")
    @ApiOperation("GetAll CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> getAllCountrySolverConfig() {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,countrySolverConfigService.getAllCountrySolverConfig());
    }
    /**
     * Always modification no object creation so,Patch
     * @param solverConfigDTO
     * @return
     */
    @PatchMapping (value = "/update")
    @ApiOperation("Update CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> updateCountrySolverConfig(@RequestBody SolverConfigDTO solverConfigDTO) {
        countrySolverConfigService.updateCountrySolverConfig(solverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.ACCEPTED);
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation("Delete CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> deleteCountrySolverConfig(@RequestParam String solverConfigId) {
        countrySolverConfigService.deleteCountrySolverConfig(solverConfigId);
        return ResponseHandler.generateResponse("Success", HttpStatus.GONE);
    }

}
