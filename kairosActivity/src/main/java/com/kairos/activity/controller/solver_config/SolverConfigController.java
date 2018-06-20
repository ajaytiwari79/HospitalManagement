package com.kairos.activity.controller.solver_config;

import com.kairos.activity.constants.ApiConstants;
import com.kairos.activity.service.solver_config.SolverConfigService;
import com.kairos.activity.util.response.ResponseHandler;
import com.kairos.dto.solverconfig.SolverConfigDTO;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author pradeep
 * @date - 20/6/18
 */

@RestController
@RequestMapping(ApiConstants.API_ORGANIZATION_UNIT_URL + "/solverConfig")
public class SolverConfigController {
    private static Logger logger= LoggerFactory.getLogger(SolverConfigController.class);
    @Autowired
    private SolverConfigService solverConfigService;

    @PostMapping(value = "/")
    @ApiOperation("Create solver config")
    public ResponseEntity<Map<String, Object>> addSolverConfig(@RequestBody SolverConfigDTO solverConfigDTO,
                                                               @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true, solverConfigService.createSolverConfig(unitId, solverConfigDTO));
    }

    @GetMapping(value = "/getAll")
    @ApiOperation("get All solver config")
    public ResponseEntity<Map<String, Object>> getAllSolverConfig(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, solverConfigService.getAllVRPSolverConfig(unitId));
    }

    @PutMapping(value = "")
    @ApiOperation("update solver config")
    public ResponseEntity<Map<String, Object>> updateSolverConfig(@PathVariable Long unitId,@RequestBody SolverConfigDTO solverConfigDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, solverConfigService.updateSolverConfig(unitId,solverConfigDTO));
    }

    @PostMapping(value = "/defaultConfig")
    @ApiOperation("create default solver config")
    public ResponseEntity<Map<String, Object>> createDefaultConfig(@PathVariable Long unitId) {
        solverConfigService.createDefaultConfig(unitId);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,null);
    }

}

