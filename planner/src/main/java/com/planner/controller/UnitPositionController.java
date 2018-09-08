package com.planner.controller;

import com.kairos.dto.activity.wta.UnitPositionWtaDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.planner.commonUtil.ResponseHandler;
import com.planner.service.staff.UnitPositionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.planner.constants.ApiConstants.API_UNIT_URL;

@RestController
@RequestMapping(API_UNIT_URL+"/unitposition")
public class UnitPositionController {
    @Autowired
    private UnitPositionService unitPositionService;
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation("add unit position")
    public ResponseEntity<Map<String, Object>> addUnitPosition(@RequestBody UnitPositionWtaDTO unitPositionDTO,
                                                               @PathVariable Long unitId) {
        unitPositionService.addUnitPosition(unitId,unitPositionDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.OK);

    }
    @RequestMapping(value = "/multiple", method = RequestMethod.POST)
    @ApiOperation("add unit position")
    public ResponseEntity<Map<String, Object>> addUnitPositions(@RequestBody List<UnitPositionWtaDTO> unitPositionDTOs,
                                                               @PathVariable Long unitId) {
        unitPositionService.addUnitPositions(unitId,unitPositionDTOs);
        return ResponseHandler.generateResponse("Success",HttpStatus.OK);

    }
    @RequestMapping(value = "/{unitPositionKairosId}", method = RequestMethod.PUT)
    @ApiOperation("update unit position")
    public ResponseEntity<Map<String, Object>> updateUnitPosition(@RequestBody UnitPositionWtaDTO unitPositionDTO,
                                                               @PathVariable Long unitId,@PathVariable Long unitPositionKairosId) {
        unitPositionService.updateUnitPosition(unitId,unitPositionKairosId,unitPositionDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.OK);

    }
    @ApiOperation(value = "Remove unit_position")
    @RequestMapping(value = "/{unitPositionId}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteUnitPosition(@PathVariable Long unitId, @PathVariable Long unitPositionId) {
        unitPositionService.removePosition(unitPositionId,unitId);
        return ResponseHandler.generateResponse("Success",HttpStatus.OK);
    }
    @ApiOperation(value = "add wta")
    @RequestMapping(value = "/{unitPositionId}/wta", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updateWTA(@PathVariable Long unitId, @PathVariable Long unitPositionId,@RequestBody WTAResponseDTO wtaResponseDTO) {
        unitPositionService.updateWTA(unitPositionId,unitId,wtaResponseDTO);
        return ResponseHandler.generateResponse("Success",HttpStatus.OK);
    }

}
