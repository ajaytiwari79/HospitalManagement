package com.planner.controller;

import com.kairos.dto.planner.organization.solverconfig.OrganizationSolverConfigDTO;
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
    public ResponseEntity<Map<String, Object>> createUnitSolverConfig(@RequestBody OrganizationSolverConfigDTO organizationSolverConfigDTO) {
        unitSolverConfigService.createUnitSolverConfig(organizationSolverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @PostMapping (value = "/copy")
    @ApiOperation("Copy UnitSolverConfig")
    public ResponseEntity<Map<String, Object>> copyUnitSolverConfig(@RequestBody OrganizationSolverConfigDTO organizationSolverConfigDTO) {
        unitSolverConfigService.copyUnitSolverConfig(organizationSolverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @GetMapping(value = "/{solverConfigId}")
    @ApiOperation("Get UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> getUnitSolverConfig(@PathVariable BigInteger organizationSolverConfigId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,unitSolverConfigService.getUnitSolverConfig(organizationSolverConfigId));
    }

    @GetMapping
    @ApiOperation("GetAll UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> getAllUnitSolverConfig() {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,unitSolverConfigService.getAllUnitSolverConfig());
    }
    /**
     * Always modification no object creation so,Patch
     * @param organizationSolverConfigDTO
     * @return
     */
    @PatchMapping
    @ApiOperation("Update UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> updateUnitSolverConfig(@RequestBody OrganizationSolverConfigDTO organizationSolverConfigDTO) {
        unitSolverConfigService.updateUnitSolverConfig(organizationSolverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.ACCEPTED);
    }

    @DeleteMapping
    @ApiOperation("Delete UnitSolverConfigration")
    public ResponseEntity<Map<String, Object>> deleteUnitSolverConfig(@RequestParam BigInteger organizationSolverConfigId) {
        unitSolverConfigService.deleteUnitSolverConfig(organizationSolverConfigId);
        return ResponseHandler.generateResponse("Success", HttpStatus.GONE);
    }


    @GetMapping("/default_data")
    @ApiOperation("Get DefaultData")
    public ResponseEntity<Map<String, Object>> getDefaultData(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,unitSolverConfigService.getDefaultData(unitId));
    }
}
