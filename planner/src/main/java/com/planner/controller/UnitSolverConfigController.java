package com.planner.controller;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.solverconfiguration.UnitSolverConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_PARENT_ORGANIZATION_UNIT_SOLVER_CONFIG_URL;

@RestController
@RequestMapping(value = API_PARENT_ORGANIZATION_UNIT_SOLVER_CONFIG_URL)
public class UnitSolverConfigController {
    @Inject
    private UnitSolverConfigService unitSolverConfigService;
    @PostMapping
    @ApiOperation("Create UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> createUnitSolverConfig(@RequestBody SolverConfigDTO solverConfigDTO) {
        unitSolverConfigService.createUnitSolverConfig(solverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @GetMapping(value = "/{solverConfigId}")
    @ApiOperation("Get UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> getUnitSolverConfig(@PathVariable String solverConfigId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,unitSolverConfigService.getUnitSolverConfig(solverConfigId));
    }

    @GetMapping
    @ApiOperation("GetAll UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> getAllUnitSolverConfig() {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,unitSolverConfigService.getAllUnitSolverConfig());
    }
    /**
     * Always modification no object creation so,Patch
     * @param solverConfigDTO
     * @return
     */
    @PatchMapping
    @ApiOperation("Update UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> updateUnitSolverConfig(@RequestBody SolverConfigDTO solverConfigDTO) {
        unitSolverConfigService.updateUnitSolverConfig(solverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.ACCEPTED);
    }

    @DeleteMapping
    @ApiOperation("Delete UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> deleteUnitSolverConfig(@RequestParam String solverConfigId) {
        unitSolverConfigService.deleteUnitSolverConfig(solverConfigId);
        return ResponseHandler.generateResponse("Success", HttpStatus.GONE);
    }


    @GetMapping("/default_data")
    @ApiOperation("Get DefaultData")
    public ResponseEntity<Map<String, Object>> getDefaultData(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,unitSolverConfigService.getDefaultData(unitId));
    }
}
