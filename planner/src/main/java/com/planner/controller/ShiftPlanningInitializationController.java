package com.planner.controller;

import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import com.planner.service.shift_planning.CTAService;
import com.planner.service.shift_planning.ShiftPlanningInitializationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * For testing
 *
 * @author mohit
 */
@RestController
@RequestMapping("/opta")
public class ShiftPlanningInitializationController {
    @Inject
    private ShiftPlanningInitializationService shiftPlanningInitializationService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private CTAService ctaService;

    /**
     * @return
     */

    ResponseEntity<Map<String, Object>> getActivities() {
        return null;//ResponseHandler.generateResponseWithData(" Data fetched sucessFully", HttpStatus.FOUND, shiftPlanningInitializationService.getActivities(2567L, new Date(1530383400000l), new Date(1532975400000l)));
    }

    /**
     * @return
     */


    @PostMapping(value = "/shiftPlanningInitialization")
    ResponseEntity<Map<String, Object>> initializeShiftPlanning(@RequestBody ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO) {
        Long[] staffIds = {34246L, 27075L, 27079L, 34251L, 34246L, 34272L};
        shiftPlanningInitializationService.initializeShiftPlanning(shiftPlanningProblemSubmitDTO);

        return ResponseHandler.generateResponse(" Data fetched sucessFully", HttpStatus.FOUND);
    }

    /*@RequestMapping("/getWTA")
    ResponseEntity<Map<String, Object>> getWTA() {
        Long[] unitPositionIds = {35682L,35681L};
        return ResponseHandler.generateResponseWithData(" Data fetched sucessFully", HttpStatus.FOUND, activityMongoRepository.getWTARuleTemplateByUnitPositionIds(unitPositionIds));
    }

    @RequestMapping("/getShifts")
    ResponseEntity<Map<String, Object>> getShifts() {
        Long[] unitPositionIds = {35682L,35681L};
        return ResponseHandler.generateResponseWithData(" Data fetched sucessFully", HttpStatus.FOUND, activityMongoRepository.getAllShiftsByUnitPositionIds(unitPositionIds, new Date(1530383400000l), new Date(1532975400000l)));
    }*/
}
