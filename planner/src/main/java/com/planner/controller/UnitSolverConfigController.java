package com.planner.controller;

import com.kairos.dto.planner.solverconfig.unit.UnitSolverConfigDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.solverconfiguration.UnitSolverConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_PARENT_ORGANIZATION_UNIT_SOLVER_CONFIG_URL;

@RestController
@RequestMapping(value = API_PARENT_ORGANIZATION_UNIT_SOLVER_CONFIG_URL)
public class UnitSolverConfigController {
    public static final String SUCCESS = "Success";
    @Inject
    private UnitSolverConfigService unitSolverConfigService;

    @PostMapping
    @ApiOperation("Create UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> createUnitSolverConfig(@PathVariable Long unitId,@RequestBody @Valid UnitSolverConfigDTO unitSolverConfigDTO) {
        return ResponseHandler.generateResponseWithData(SUCCESS, HttpStatus.OK,unitSolverConfigService.createUnitSolverConfig(unitSolverConfigDTO,unitId));
    }

    @PostMapping(value = "/copy")
    @ApiOperation("Copy UnitSolverConfig")
    public ResponseEntity<Map<String, Object>> copyUnitSolverConfig(@RequestBody UnitSolverConfigDTO unitSolverConfigDTO) {
        unitSolverConfigService.copyUnitSolverConfig(unitSolverConfigDTO);
        return ResponseHandler.generateResponse(SUCCESS, HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation("Get UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> getAllUnitSolverConfigByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseWithData(SUCCESS, HttpStatus.OK, unitSolverConfigService.getAllUnitSolverConfigByUnitId(unitId));
    }


    /**
     * Always modification no object creation so,Patch
     *
     * @param unitSolverConfigDTO
     * @return
     */
    @PutMapping(value = "/{unitSolverConfigId}")
    @ApiOperation("Update UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> updateUnitSolverConfig(@PathVariable Long unitId,@RequestBody @Valid UnitSolverConfigDTO unitSolverConfigDTO) {
        return ResponseHandler.generateResponseWithData(SUCCESS, HttpStatus.OK,unitSolverConfigService.updateUnitSolverConfig(unitId,unitSolverConfigDTO));
    }

    @DeleteMapping(value = "/{unitSolverConfigId}")
    @ApiOperation("Delete UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> deleteUnitSolverConfig(@PathVariable BigInteger unitSolverConfigId) {
        unitSolverConfigService.deleteUnitSolverConfig(unitSolverConfigId);
        return ResponseHandler.generateResponse(SUCCESS, HttpStatus.OK);
    }


    @GetMapping("/default_data")
    @ApiOperation("Get DefaultData")
    public ResponseEntity<Map<String, Object>> getDefaultData(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseWithData(SUCCESS, HttpStatus.OK, unitSolverConfigService.getDefaultData(unitId));
    }
}
