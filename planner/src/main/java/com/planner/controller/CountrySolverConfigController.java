package com.planner.controller;

import com.planner.commonUtil.ResponseHandler;
import com.planner.service.solverconfiguration.CountrySolverConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

@RestController
@RequestMapping
public class CountrySolverConfigController {

    @Inject
    private CountrySolverConfigService  countrySolverConfigService;

    @PostMapping (value = "/")
    @ApiOperation("Create CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> createCountrySolverConfig() {

        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @GetMapping(value = "/")
    @ApiOperation("Create CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> getCountrySolverConfig() {

        return ResponseHandler.generateResponse("Success", HttpStatus.FOUND);
    }
    @PutMapping (value = "/")
    @ApiOperation("Update CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> updateCountrySolverConfig() {

        return ResponseHandler.generateResponse("Success", HttpStatus.ACCEPTED);
    }

    @DeleteMapping(value = "/")
    @ApiOperation("Delete CountrySolverConfigration")
    public ResponseEntity<Map<String, Object>> deleteCountrySolverConfig() {

        return ResponseHandler.generateResponse("Success", HttpStatus.GONE);
    }

}
