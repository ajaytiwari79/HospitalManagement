package com.planner.controller;

import com.kairos.dto.planner.solverconfig.unit.UnitSolverConfigDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.solverconfiguration.UnitSolverConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_PARENT_ORGANIZATION_UNIT_SOLVER_CONFIG_URL;

@RestController
@RequestMapping(value = API_PARENT_ORGANIZATION_UNIT_SOLVER_CONFIG_URL)
public class UnitSolverConfigController {
    @Inject
    private UnitSolverConfigService unitSolverConfigService;
    @PostMapping
    @ApiOperation("Create UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> createUnitSolverConfig(@RequestBody UnitSolverConfigDTO unitSolverConfigDTO) {
        unitSolverConfigService.createUnitSolverConfig(unitSolverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @PostMapping (value = "/copy")
    @ApiOperation("Copy UnitSolverConfig")
    public ResponseEntity<Map<String, Object>> copyUnitSolverConfig(@RequestBody UnitSolverConfigDTO unitSolverConfigDTO) {
        unitSolverConfigService.copyUnitSolverConfig(unitSolverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @GetMapping(value = "/{unitSolverConfigId}")
    @ApiOperation("Get UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> getUnitSolverConfigById(@PathVariable BigInteger unitSolverConfigId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND, unitSolverConfigService.getUnitSolverConfig(unitSolverConfigId));
    }

    @GetMapping
    @ApiOperation("GetAll UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> getAllUnitSolverConfig() {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND, unitSolverConfigService.getAllUnitSolverConfig());
    }
    /**
     * Always modification no object creation so,Patch
     * @param unitSolverConfigDTO
     * @return
     */
    @PatchMapping
    @ApiOperation("Update UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> updateUnitSolverConfig(@RequestBody UnitSolverConfigDTO unitSolverConfigDTO) {
        unitSolverConfigService.updateUnitSolverConfig(unitSolverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.ACCEPTED);
    }

    @DeleteMapping(value = "/{unitSolverConfigId}")
    @ApiOperation("Delete UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> deleteUnitSolverConfig(@PathVariable BigInteger unitSolverConfigId) {
        unitSolverConfigService.deleteUnitSolverConfig(unitSolverConfigId);
        return ResponseHandler.generateResponse("Success", HttpStatus.GONE);
    }


    @GetMapping("/default_data")
    @ApiOperation("Get DefaultData")
    public ResponseEntity<Map<String, Object>> getDefaultData(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND, unitSolverConfigService.getDefaultData(unitId));
    }
}
