package com.planner.controller;

import com.kairos.dto.planner.constarints.unit.UnitConstraintDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.constraint.unit.UnitConstraintService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_PARENT_ORGANIZATION_UNIT_CONSTRAINT_URL;

@RestController
@RequestMapping(API_PARENT_ORGANIZATION_UNIT_CONSTRAINT_URL)
public class UnitConstraintController {

    @Inject
    private UnitConstraintService unitConstraintService;

    //=============================================================
    @PostMapping
    @ApiOperation("Create UnitConstraint")
    public ResponseEntity<Map<String, Object>> createUnitConstraint(@RequestBody UnitConstraintDTO unitConstraintDTO) {
        unitConstraintService.createUnitConstraint(unitConstraintDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @PostMapping(value = "/copy")
    @ApiOperation("Copy UnitConstraint")
    public ResponseEntity<Map<String, Object>> copyUnitConstraint(@RequestBody UnitConstraintDTO unitConstraintDTO) {
        unitConstraintService.copyUnitConstraint(unitConstraintDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    @GetMapping
    @ApiOperation("Get UnitConstraint")
    public ResponseEntity<Map<String, Object>> getUnitConstraintsByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND, unitConstraintService.getUnitConstraintsByUnitId(unitId));
    }


    @PatchMapping
    @ApiOperation("Update UnitConstraintration")
    public ResponseEntity<Map<String, Object>> updateUnitConstraint(@RequestBody UnitConstraintDTO unitConstraintDTO) {
        unitConstraintService.updateUnitConstraint(unitConstraintDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.ACCEPTED);
    }

    @DeleteMapping(value = "/{unitConstraintId}")
    @ApiOperation("Delete UnitConstraintration")
    public ResponseEntity<Map<String, Object>> deleteUnitConstraint(@PathVariable BigInteger unitConstraintId) {
        unitConstraintService.deleteUnitConstraint(unitConstraintId);
        return ResponseHandler.generateResponse("Success", HttpStatus.GONE);
    }

}
