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


import static com.planner.constants.ApiConstants.API_UNIT_URL;


@RestController
@RequestMapping(API_UNIT_URL)
public class ActivityConstraintController {
    public static final String SUCCESS = "Success";

   @Inject
   private ActivityConstraintService activityConstraintService;


    @PostMapping(value = "/createOrUpdateActivityConstraint")
    @ApiOperation("Create Or Update ActivityConstraint")
    public ResponseEntity<Map<String, Object>> createActivityConstraint(@RequestBody  @Valid ActivityConstraintDTO activityConstraintDTO) {
        return ResponseHandler.generateResponseWithData(SUCCESS, HttpStatus.OK, activityConstraintService.createOrUpdateActivityConstraint(activityConstraintDTO));
    }

    @GetMapping(value="/activityConstraints")
    @ApiOperation("Get All  ActivityConstraint")
    public ResponseEntity<Map<String, Object>> getActivityConstraints() {
        return ResponseHandler.generateResponseWithData(SUCCESS, HttpStatus.OK,activityConstraintService.getActivityConstraints());
    }


    @GetMapping(value="activity/{activityId}/activityConstraints")
    @ApiOperation("Get ActivityConstraint")
    public ResponseEntity<Map<String, Object>> getAllActivityConstraintByActivityId(@PathVariable BigInteger activityId) {
        return ResponseHandler.generateResponseWithData(SUCCESS, HttpStatus.OK,activityConstraintService.getAllActivityConstraintByActivityId(activityId));
    }








}
