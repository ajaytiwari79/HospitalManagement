package com.planner.controller;

import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.constraint.ConstraintService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_PARENT_ORGANIZATION_COUNTRY_CONSTRAINT_URL;

@RestController
@RequestMapping(API_PARENT_ORGANIZATION_COUNTRY_CONSTRAINT_URL)
public class ConstraintController {


    @Inject
    private ConstraintService constraintService;
    
    //=============================================================================
    @PostMapping
    @ApiOperation("Create Constraint")
    public ResponseEntity<Map<String, Object>> createConstraint(@RequestBody ConstraintDTO constraintDTO) {
       // constraintService.createConstraint(constraintDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.CREATED);
    }

    //=============================================================================
    @GetMapping(value = "/{constraintId}")
    @ApiOperation("Get Constraint")
    public ResponseEntity<Map<String, Object>> getConstraint(@PathVariable BigInteger constraintId) {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,constraintService.getConstraint(constraintId));
    }
    //=============================================================================
    @GetMapping
    @ApiOperation("GetAll Constraint")
    public ResponseEntity<Map<String, Object>> getAllConstraint() {
        return ResponseHandler.generateResponseWithData("Success", HttpStatus.FOUND,constraintService.getAllConstraint());
    }

    //=============================================================================
    /**
     * Always modification no object creation so,Patch
     * @param constraintDTO
     * @return
     */
    @PatchMapping
    @ApiOperation("Update Constraint")
    public ResponseEntity<Map<String, Object>> updateConstraint(@RequestBody ConstraintDTO constraintDTO) {
        //constraintService.updateConstraint(constraintDTO);
        return ResponseHandler.generateResponse("Success", HttpStatus.ACCEPTED);
    }
    //=============================================================================
    @DeleteMapping
    @ApiOperation("Delete Constraint")
    public ResponseEntity<Map<String, Object>> deleteConstraint(@RequestParam BigInteger constraintId) {
        constraintService.deleteConstraint(constraintId);
        return ResponseHandler.generateResponse("Success", HttpStatus.GONE);
    }
}
