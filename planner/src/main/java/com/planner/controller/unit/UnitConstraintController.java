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
import static com.planner.constants.ApiConstants.API_V1;



@RestController
@RequestMapping(API_V1)
public class UnitConstraintController {
    public static final String SUCCESS = "Success";

    @Inject
    private UnitConstraintService unitConstraintService;


    @PostMapping(value="/createOrUpdateUnitConstraint")
    @ApiOperation("Create UnitConstraint")
    public ResponseEntity<Map<String, Object>> createUnitConstraint(@RequestBody @Valid UnitConstraintDTO unitConstraintDTO) {
        return ResponseHandler.generateResponseWithData(SUCCESS,HttpStatus.OK,unitConstraintService.createUnitConstraint(unitConstraintDTO));
    }

    @GetMapping(value="/unit/{unitId}/unitConstraints")
    @ApiOperation("Get UnitConstraint")
    public ResponseEntity<Map<String, Object>> getAllUnitConstraintByUnitId(@PathVariable Long unitId) {
        return ResponseHandler.generateResponseWithData(SUCCESS, HttpStatus.OK,unitConstraintService.getAllUnitConstraintByUnitId(unitId));
    }

    @GetMapping(value="/unitConstraints")
    @ApiOperation("Get All  UnitConstraint")
    public ResponseEntity<Map<String, Object>> getUnitConstraints() {
        return ResponseHandler.generateResponseWithData(SUCCESS, HttpStatus.OK,unitConstraintService.getunitconstraints());
    }





}
