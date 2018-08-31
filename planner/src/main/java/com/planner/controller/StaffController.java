package com.planner.controller;


import com.planner.commonUtil.ResponseHandler;
import com.planner.service.staff.StaffService;
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
@RequestMapping()
public class StaffController {
    /*private static Logger logger= LoggerFactory.getLogger(StaffController.class);
    @Autowired
    private StaffService staffService;
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation("Create staff")
    public ResponseEntity<Map<String, Object>> addStaffingLevel(@RequestBody StaffBasicDetailsDTO staffDTO,
                                                                @PathVariable Long unitId) {
        staffService.createStaff(unitId,staffDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.CREATED);
    }

    @RequestMapping(value = "/multiple", method = RequestMethod.POST)
    @ApiOperation("Create bulk staff")
    public ResponseEntity<Map<String, Object>> addStaffingLevel(@RequestBody List<StaffBasicDetailsDTO> staffDTOs,
                                                                @PathVariable Long unitId) {
        staffService.createStaff(unitId,staffDTOs);
        return ResponseHandler.generateResponse("Success",HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{staffingLevelKairosId}", method = RequestMethod.PUT)
    @ApiOperation("update staffing_level")
    public ResponseEntity<Map<String, Object>> updateStaffingLevel(@RequestBody  StaffBasicDetailsDTO staffDTO,
                                                                   @PathVariable Long unitId, @PathVariable Long staffKairosId) {
        staffService.updateStaff(staffKairosId,unitId,staffDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.OK);

    }*/

    @Autowired
    private StaffService staffService;
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ApiOperation("Create staff")
    public ResponseEntity<Map<String, Object>> addStaffingLevel() {

        return ResponseHandler.generateResponseWithData("Success",HttpStatus.CREATED, staffService.test());
    }


}
