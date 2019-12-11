package com.planner.controller.unit;


import com.kairos.dto.activity.activity.ActivityConstraintDTO;
import com.kairos.dto.planner.constarints.unit.UnitConstraintDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.constraint.unit.UnitConstraintService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL+"/unitConstraint")
public class UnitConstraintController {
    public static final String SUCCESS = "Success";

    @Inject
    private UnitConstraintService unitConstraintService;


    @PostMapping
    @ApiOperation("Create UnitConstraint")
    public ResponseEntity<Map<String, Object>> createUnitConstraint(@RequestBody @Valid UnitConstraintDTO unitConstraintDTO) {


        unitConstraintService.createUnitConstraint(unitConstraintDTO);
        return ResponseHandler.generateResponse(SUCCESS, HttpStatus.OK);
    }

    @GetMapping(value="/{unitId}")
    @ApiOperation("Get UnitConstraint")
    public ResponseEntity<Map<String, Object>> getAllUnitConstraintByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseWithData(SUCCESS, HttpStatus.OK,unitConstraintService.getAllUnitConstraintByUnitId(unitId));
    }

    @PatchMapping
    @ApiOperation("Update UnitConstraint")
    public ResponseEntity<Map<String, Object>> updateUnitConstraint(@RequestBody @Valid UnitConstraintDTO unitConstraintDTO) {
        unitConstraintService.updateUnitConstraint(unitConstraintDTO);
        return ResponseHandler.generateResponse(SUCCESS, HttpStatus.OK);
    }





}
