package com.planner.controller;

import com.kairos.dto.solverconfig.SolverConfigDTO;
import com.kairos.persistence.model.user.staff.StaffBasicDetailsDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.config.SolverConfigService;
import com.planner.service.staff.StaffService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.planner.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL + "/solverConfig")
public class SolverConfigController {
    private static Logger logger= LoggerFactory.getLogger(StaffController.class);
    @Autowired
    private SolverConfigService solverConfigService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation("Create solver config")
    public ResponseEntity<Map<String, Object>> addSolverConfig(@RequestBody SolverConfigDTO solverConfigDTO,
                                                                @PathVariable Long unitId) {
        solverConfigService.addSolverConfig(unitId, solverConfigDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.CREATED);
    }

}
