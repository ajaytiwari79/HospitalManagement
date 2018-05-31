package com.planner.controller;

import com.kairos.activity.response.dto.staffing_level.PresenceStaffingLevelDto;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelDTO;
import com.kairos.activity.response.dto.staffing_level.StaffingLevelDto;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.staffinglevel.StaffingLevelService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL + "/staffing_level")
public class StaffingLevelController {
    private Logger logger= LoggerFactory.getLogger(StaffingLevelController.class);
    @Autowired
    private StaffingLevelService staffingLevelService;
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation("Create staffing_level")
    public ResponseEntity<Map<String, Object>> addStaffingLevel(@RequestBody @Valid StaffingLevelDTO staffingLevelDto,
                                                @PathVariable Long unitId) {
        staffingLevelService.createStaffingLevel(unitId,staffingLevelDto);
        return ResponseHandler.generateResponse("Success",HttpStatus.CREATED);
    }
    @RequestMapping(value = "/multiple", method = RequestMethod.POST)
    @ApiOperation("Create staffing_level")
    public ResponseEntity<Map<String, Object>> addStaffingLevels(@RequestBody @Valid List<StaffingLevelDTO> staffingLevelDtos,
                                                                 @PathVariable Long unitId) {
        staffingLevelService.createStaffingLevels(unitId,staffingLevelDtos);
        return ResponseHandler.generateResponse("Success",HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{staffingLevelKairosId}", method = RequestMethod.PUT)
    @ApiOperation("update staffing_level")
    public ResponseEntity<Map<String, Object>> updateStaffingLevel(@RequestBody @Valid StaffingLevelDTO staffingLevelDto,
                                                                   @PathVariable Long unitId, @PathVariable BigInteger staffingLevelKairosId) {
        staffingLevelService.updateStaffingLevel(staffingLevelKairosId,unitId,staffingLevelDto);
        return ResponseHandler.generateResponse("Success",HttpStatus.OK);

    }

}
