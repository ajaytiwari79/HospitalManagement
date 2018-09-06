package com.planner.controller;

import com.planner.commonUtil.ResponseHandler;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import com.planner.service.shift_planning.ShiftPlanningInitializationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

/**
 * For testing
 *
 * @author mohit
 */
@RestController()
public class ShiftPlanningInitializationController {
    @Inject
    private ShiftPlanningInitializationService shiftPlanningInitializationService;
@Inject
private ActivityMongoRepository activityMongoRepository;

    /**
     *
     *
     * @return
     */
    @RequestMapping("/shiftPlanningInitialization")
    ResponseEntity<Map<String, Object>> getActivities() {
        return ResponseHandler.generateResponseWithData(" Data fetched sucessFully", HttpStatus.FOUND, shiftPlanningInitializationService.getActivities(2567L, new Date(1530383400000l), new Date(1532975400000l)));
    }

    /**
     * @return
     */
    @RequestMapping("/getStaff")
    ResponseEntity<Map<String, Object>> getStaffWithSkills() {
        Long unitId=25120L;
        Long[] staffIds = {34246L,27575L};
        return ResponseHandler.generateResponseWithData(" Data fetched sucessFully", HttpStatus.FOUND, shiftPlanningInitializationService.getStaffWithSkillsAndUnitPostionIds(unitId,staffIds));

    }
    @RequestMapping("/getCTA")
    ResponseEntity<Map<String, Object>> getCTA() {
        Long[] unitPositionIds = {35682L,35681L};
        return ResponseHandler.generateResponseWithData(" Data fetched sucessFully", HttpStatus.FOUND, activityMongoRepository.getCTARuleTemplateByUnitPositionIds(unitPositionIds));
    }
}
