package com.planner.controller;

import com.kairos.response.dto.web.UnitPositionWtaDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.staff.UnitPositionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.planner.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL + "/staff/{staffId}/unitposition")
public class UnitPositionController {
    @Autowired
    private UnitPositionService unitPositionService;
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation("add unit position")
    public ResponseEntity<Map<String, Object>> addUnitPosition(@RequestBody UnitPositionWtaDTO unitPositionDTO,
                                                               @PathVariable Long unitId,@PathVariable(name = "staffId") Long staffKairosId) {
        unitPositionService.addUnitPosition(staffKairosId,unitId,unitPositionDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.OK);

    }
    @RequestMapping(value = "/{unitPositionKairosId}", method = RequestMethod.PUT)
    @ApiOperation("update unit position")
    public ResponseEntity<Map<String, Object>> updateUnitPosition(@RequestBody UnitPositionWtaDTO unitPositionDTO,
                                                               @PathVariable Long unitId,@PathVariable(name = "staffId") Long staffKairosId,@PathVariable Long unitPositionKairosId) {
        unitPositionService.updateUnitPosition(staffKairosId,unitId,unitPositionKairosId,unitPositionDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.OK);

    }

}
