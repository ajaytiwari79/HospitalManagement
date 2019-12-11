package com.planner.controller.activity;

import com.kairos.dto.activity.activity.ActivityConstraintDTO;
import com.kairos.dto.planner.constarints.country.CountryConstraintDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.constraint.activity.ActivityConstraintService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_ACTIVITY_CONSTRAINT;



@RestController
@RequestMapping(API_ACTIVITY_CONSTRAINT)
public class ActivityConstraintController {
    public static final String SUCCESS = "Success";

   @Inject
   private ActivityConstraintService activityConstraintService;


    @PostMapping
    @ApiOperation("Create ActivityConstraint")
    public ResponseEntity<Map<String, Object>> createActivityConstraint(@RequestBody  @Valid ActivityConstraintDTO activityConstraintDTO) {

         activityConstraintService.createActivityConstraint(activityConstraintDTO);
        return ResponseHandler.generateResponse(SUCCESS, HttpStatus.OK);
    }

    @GetMapping(value="/{activityId}")
    @ApiOperation("Get ActivityConstraint")
    public ResponseEntity<Map<String, Object>> getAllActivityConstraintByActivityId(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponseWithData(SUCCESS, HttpStatus.OK,activityConstraintService.getAllActivityConstraintByActivityId(activityId));
    }

    @PutMapping
    @ApiOperation("Update ActivityConstraint")
    public ResponseEntity<Map<String, Object>> updateActivityConstraint(@RequestBody @Valid ActivityConstraintDTO activityConstraintDTO) {
        return ResponseHandler.generateResponseWithData(SUCCESS, HttpStatus.OK,activityConstraintService.updateActivityConstraint(activityConstraintDTO));
    }








}
