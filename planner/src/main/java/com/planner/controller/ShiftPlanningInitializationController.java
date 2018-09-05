package com.planner.controller;

import com.planner.commonUtil.ResponseHandler;
import com.planner.repository.shift_planning.UserNeo4jRepo;
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
 *@author mohit
 */
@RestController()
public class ShiftPlanningInitializationController {
    @Inject
    private ShiftPlanningInitializationService shiftPlanningInitializationService;
  @Inject
  private UserNeo4jRepo userNeo4jRepo;
    /**
     *method to prepare data for shift planning initialization
     * @return
     */
    @RequestMapping("/shiftPlanningInitialization")
    ResponseEntity<Map<String, Object>> getStaffingLevel( ) {
        return ResponseHandler.generateResponseWithData(" Data fetched sucessFully", HttpStatus.FOUND,shiftPlanningInitializationService.getActivities(2567L,new Date(1530383400000l),new Date(1532975400000l)));
    }

    @RequestMapping("/getStaff")
    ResponseEntity<Map<String, Object>> getStaffByIds( ) {
        Long[] staffIds={27435L};
        return ResponseHandler.generateResponseWithData(" Data fetched sucessFully", HttpStatus.FOUND,userNeo4jRepo.getStaffData(staffIds));
    }
}
