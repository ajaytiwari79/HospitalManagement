package com.planner.controller;

import com.kairos.dto.planner.organization.solverconfig.OrganizationSolverConfigDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.solverconfiguration.OrganizationSolverConfigService;
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
public class OrganizationSolverConfigController {
    @Inject
    private OrganizationSolverConfigService organizationSolverConfigService;
    @PostMapping
    @ApiOperation("Create OrganizationSolverConfigration")
    public ResponseEntity<Map<String, Object>> createOrganizationSolverConfig(@RequestBody OrganizationSolverConfigDTO organizationSolverConfigDTO) {
        organizationSolverConfigService.createOrganizationSolverConfig(organizationSolverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @PostMapping (value = "/copy")
    @ApiOperation("Copy OrganizationSolverConfig")
    public ResponseEntity<Map<String, Object>> copyOrganizationSolverConfig(@RequestBody OrganizationSolverConfigDTO organizationSolverConfigDTO) {
        organizationSolverConfigService.copyOrganizationSolverConfig(organizationSolverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @GetMapping(value = "/{organizationSolverConfigId}")
    @ApiOperation("Get OrganizationSolverConfigration")
    public ResponseEntity<Map<String, Object>> getOrganizationSolverConfigById(@PathVariable BigInteger organizationSolverConfigId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,organizationSolverConfigService.getOrganizationSolverConfig(organizationSolverConfigId));
    }

    @GetMapping
    @ApiOperation("GetAll OrganizationSolverConfigration")
    public ResponseEntity<Map<String, Object>> getAllOrganizationSolverConfig() {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,organizationSolverConfigService.getAllOrganizationSolverConfig());
    }
    /**
     * Always modification no object creation so,Patch
     * @param organizationSolverConfigDTO
     * @return
     */
    @PatchMapping
    @ApiOperation("Update OrganizationSolverConfigration")
    public ResponseEntity<Map<String, Object>> updateOrganizationSolverConfig(@RequestBody OrganizationSolverConfigDTO organizationSolverConfigDTO) {
        organizationSolverConfigService.updateOrganizationSolverConfig(organizationSolverConfigDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.ACCEPTED);
    }

    @DeleteMapping(value = "/{organizationSolverConfigId}")
    @ApiOperation("Delete OrganizationSolverConfigration")
    public ResponseEntity<Map<String, Object>> deleteOrganizationSolverConfig(@PathVariable BigInteger organizationSolverConfigId) {
        organizationSolverConfigService.deleteOrganizationSolverConfig(organizationSolverConfigId);
        return ResponseHandler.generateResponse("Success", HttpStatus.GONE);
    }


    @GetMapping("/default_data")
    @ApiOperation("Get DefaultData")
    public ResponseEntity<Map<String, Object>> getDefaultData(@PathVariable Long organizationId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,organizationSolverConfigService.getDefaultData(organizationId));
    }
}
