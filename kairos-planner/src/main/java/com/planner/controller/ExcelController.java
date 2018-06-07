package com.planner.controller;

import com.kairos.dto.planninginfo.PlanningSubmissionDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.excel.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.planner.constants.ApiConstants.API_UNIT_URL;

/**
 * @author pradeep
 * @date - 7/6/18
 */
@RestController
@RequestMapping(API_UNIT_URL + "/excel")
public class ExcelController {

    @Autowired private ExcelService excelService;


    @RequestMapping(value = "/read", method = RequestMethod.POST)
    ResponseEntity<Map<String, Object>> readExcelFile(@PathVariable Long unitId) {
        //@RequestParam MultipartFile file,
        excelService.readExcelFile();
        return ResponseHandler.generateResponse("Read sucessFully", HttpStatus.ACCEPTED);
    }





}
