package com.planner.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.planner.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL + "/staffing_level")
public class StaffingLevelController {
    private Logger logger= LoggerFactory.getLogger(StaffingLevelController.class);



}
