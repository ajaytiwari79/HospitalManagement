package com.planner.controller;

import com.kairos.activity.response.dto.staffing_level.StaffingLevelDto;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.staffinglevel.StaffingLevelService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL + "/staffing_level")
public class StaffingLevelController {
    private Logger logger= LoggerFactory.getLogger(StaffingLevelController.class);
    private StaffingLevelService staffingLevelService;
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation("Create staffing_level")
    public Map<String, Object> addStaffingLevel(@RequestBody @Valid StaffingLevelDto staffingLevelDto,
                                                @PathVariable Long unitId) {
        staffingLevelService.createStaffingLevel(unitId,staffingLevelDto);
        return ResponseHandler.generateResponse( "",HttpStatus.CREATED);
    }

}
