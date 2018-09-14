package com.kairos.controller.solver_config;

import com.kairos.constants.ApiConstants;
import com.kairos.service.planner.vrpPlanning.VRPPlanningService;
import com.kairos.service.solver_config.SolverConfigService;
import com.kairos.utils.response.ResponseHandler;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

/**
 * @author pradeep
 * @date - 20/6/18
 */

@RestController
@RequestMapping(ApiConstants.API_ORGANIZATION_UNIT_URL + "/solverConfig")
public class SolverConfigController {
    private static Logger logger= LoggerFactory.getLogger(SolverConfigController.class);
    @Inject
    private SolverConfigService solverConfigService;
    @Inject private VRPPlanningService vrpPlanningService;

    @PostMapping(value = "")
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

    @PutMapping(value = "/{solverConfigId}")
    @ApiOperation("update solver config")
    public ResponseEntity<Map<String, Object>> updateSolverConfig(@PathVariable BigInteger solverConfigId, @PathVariable Long unitId, @RequestBody SolverConfigDTO solverConfigDTO) {
        return ResponseHandler.generateResponse(HttpStatus.ACCEPTED, true, solverConfigService.updateSolverConfig(unitId,solverConfigId,solverConfigDTO));
    }

    @PostMapping(value = "/defaultConfig")
    @ApiOperation("create default solver config")
    public ResponseEntity<Map<String, Object>> createDefaultConfig(@PathVariable Long unitId) {
        solverConfigService.createDefaultConfig(unitId);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,null);
    }

   /* @PostMapping(value = "/{solverConfigId}/stop")
    @ApiOperation("create default solver config")
    public ResponseEntity<Map<String, Object>> stopToPlannerBySolverConfig(@PathVariable Long unitId,@PathVariable BigInteger solverConfigId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,vrpPlanningService.stopToPlannerBySolverConfig(unitId,solverConfigId));
    }



    @PostMapping(value = "/{solverConfigId}")
    @ApiOperation("create default solver config")
    public ResponseEntity<Map<String, Object>> submitToPlanner(@PathVariable Long unitId,@PathVariable BigInteger solverConfigId) {
        return ResponseHandler.generateResponse(HttpStatus.CREATED, true,vrpPlanningService.submitToPlanner(unitId,solverConfigId));
    }*/

}



