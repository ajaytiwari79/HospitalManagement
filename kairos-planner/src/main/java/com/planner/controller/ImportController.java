package com.planner.controller;

import com.kairos.dto.planninginfo.PlanningSubmissionDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.importService.ImportService;
import com.planner.service.tomtomService.TomTomService;
import com.planner.service.vrpService.VRPGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.planner.constants.ApiConstants.API_UNIT_URL;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@RestController
@RequestMapping(API_UNIT_URL + "/import")
public class ImportController {

    @Autowired private ImportService importService;
    @Autowired private VRPGeneratorService vrpGeneratorService;
    @Autowired private TomTomService tomTomService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> importData(@PathVariable Long unitId) {
        importService.readExcelFile();
        return ResponseHandler.generateResponse("import Data sucessFully", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/writeToJson", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> writeToJson(@PathVariable Long unitId) {
        vrpGeneratorService.writeToJson();
        return ResponseHandler.generateResponse("import Data sucessFully", HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/getLocation", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> getLocation(@PathVariable Long unitId) {
        tomTomService.getLocationData();
        return ResponseHandler.generateResponse("import Data sucessFully", HttpStatus.ACCEPTED);
    }


}
