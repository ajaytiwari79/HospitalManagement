package com.planner.controller;

import com.kairos.activity.activity.activity_tabs.ActivityNoTabsDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.repository.activity.ActivityKairosRepository;
import com.planner.service.activity.ActivityService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_UNIT_URL;

@RestController
/*@RequestMapping(API_UNIT_URL + "/activity")*/
//Planner api are exposed based on kariosId not on it's own id because kairos would need not to know planner id.
public class ActivityController {
    private Logger logger= LoggerFactory.getLogger(StaffingLevelController.class);
    @Autowired
    private ActivityService activityService;

    @Inject
    private ActivityKairosRepository activityKairosRepository;
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation("Create Activity")
    public ResponseEntity<Map<String, Object>> addActivity(@RequestBody @Valid ActivityNoTabsDTO activityDTO,
                                                                @PathVariable Long unitId) {
        activityService.createActivity(unitId,activityDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.CREATED);
    }
    @RequestMapping(value = "/{activityKairosId}", method = RequestMethod.PUT)
    @ApiOperation("Update Activity")
    public ResponseEntity<Map<String, Object>> updateActivity(@RequestBody @Valid ActivityNoTabsDTO activityDTO,
                                                                @PathVariable Long unitId,@PathVariable BigInteger activityKairosId) {
        activityService.updateActivity(unitId,activityKairosId,activityDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.CREATED);
    }
    @RequestMapping(value = "/multiple", method = RequestMethod.POST)
    @ApiOperation("Create Activity")
    public ResponseEntity<Map<String, Object>> addActivity(@RequestBody List<ActivityNoTabsDTO> activityDTOs,
                                                           @PathVariable Long unitId) {
        activityService.createActivities(unitId,activityDTOs);
        return ResponseHandler.generateResponse("Success",HttpStatus.CREATED);
    }

    @RequestMapping("/test1")
    public ResponseEntity<Map<String, Object>> addActivity1() {

        return ResponseHandler.generateResponseWithData("Success",HttpStatus.CREATED,activityKairosRepository.m1());

    }
}
