package com.planner.controller;

import com.kairos.response.dto.web.UnitPositionDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.staff.UnitPositionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.planner.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL + "/staff/{staffId}/unitposition")
public class UnitPositionController {
    private UnitPositionService unitPositionService;
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation("add unit position")
    public ResponseEntity<Map<String, Object>> addUnitPosition(@RequestBody UnitPositionDTO unitPositionDTO,
                                                               @PathVariable Long unitId,@PathVariable(name = "staffId") Long staffKairosId) {
        unitPositionService.addUnitPosition(staffKairosId,unitId,unitPositionDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.OK);

    }
    @RequestMapping(value = "/{unitPositionKairosId}", method = RequestMethod.PUT)
    @ApiOperation("update unit position")
    public ResponseEntity<Map<String, Object>> updateUnitPosition(@RequestBody UnitPositionDTO unitPositionDTO,
                                                               @PathVariable Long unitId,@PathVariable(name = "staffId") Long staffKairosId,@PathVariable Long unitPositionKairosId) {
        unitPositionService.updateUnitPosition(staffKairosId,unitId,unitPositionKairosId,unitPositionDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.OK);

    }

}
