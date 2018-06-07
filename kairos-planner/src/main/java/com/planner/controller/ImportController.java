package com.planner.controller;

import com.kairos.dto.planninginfo.PlanningSubmissionDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.importService.ImportService;
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

    @RequestMapping(value = "/", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> importData(@PathVariable Long unitId) {
        importService.readExcelFile();
        return ResponseHandler.generateResponse("import Data sucessFully", HttpStatus.ACCEPTED);
    }
}
