package com.kairos.controller.unitEmploymentPosition;


import com.kairos.persistence.model.user.agreement.wta.WTADTO;
import com.kairos.response.dto.web.UnitEmploymentPositionDTO;
import com.kairos.service.unitEmploymentPosition.UnitEmploymentPositionService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class UnitEmploymentPositionController {

    @Inject
    private UnitEmploymentPositionService unitEmploymentPositionService;

    @ApiOperation(value = "Create a New Position")
    @PostMapping(value = "/unitEmploymentPosition")
    public ResponseEntity<Map<String, Object>> createUnitEmploymentPosition(@PathVariable Long unitId, @RequestParam("type") String type, @RequestBody @Valid UnitEmploymentPositionDTO position) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.createUnitEmploymentPosition(unitId, type, position));
    }

    /*
   * @auth vipul
   * used to get all positions of organization n by organization and staff Id
   * */
    @ApiOperation(value = "Get all unitEmploymentPosition by organization and staff")
    @RequestMapping(value = "/unitEmploymentPosition/staff/{staffId}")
    ResponseEntity<Map<String, Object>> getAllUnitEmploymentPositionsOfStaff(@PathVariable Long unitId, @RequestParam("type") String type, @PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.getAllUnitEmploymentPositionsOfStaff(unitId, staffId, type));
    }

    @ApiOperation(value = "Remove unitEmploymentPosition")
    @DeleteMapping(value = "/unitEmploymentPosition/{unitEmploymentPositionId}")
    public ResponseEntity<Map<String, Object>> deleteUnitEmploymentPosition(@PathVariable Long unitEmploymentPositionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.removePosition(unitEmploymentPositionId));
    }


    @ApiOperation(value = "Update unitEmploymentPosition")
    @PutMapping(value = "/unitEmploymentPosition/{unitEmploymentPositionId}")
    public ResponseEntity<Map<String, Object>> updateUnitEmploymentPosition(@PathVariable Long unitEmploymentPositionId, @RequestBody @Valid UnitEmploymentPositionDTO position) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.updateUnitEmploymentPosition(unitEmploymentPositionId, position));
    }

    @ApiOperation(value = "Update unitEmploymentPosition's WTA")
    @PutMapping(value = "/unitEmploymentPosition/{unitEmploymentPositionId}/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> updateUnitEmploymentPositionWTA(@PathVariable Long unitEmploymentPositionId, @PathVariable Long unitId, @PathVariable Long wtaId, @RequestBody @Valid WTADTO wtadto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.updateUnitEmploymentPositionWTA(unitId, unitEmploymentPositionId, wtaId, wtadto));
    }

    @ApiOperation(value = "get unitEmploymentPosition's WTA")
    @GetMapping(value = "/unitEmploymentPosition/{unitEmploymentPositionId}/wta")
    public ResponseEntity<Map<String, Object>> getUnitEmploymentPositionWTA(@PathVariable Long unitEmploymentPositionId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.getUnitEmploymentPositionWTA(unitId, unitEmploymentPositionId));
    }
/*
    @ApiOperation(value = "Get Position")
    @GetMapping(value = "/unitEmploymentPosition/{unitEmploymentPositionId}")
    public ResponseEntity<Map<String, Object>> getUnitEmploymentPosition(@PathVariable Long unitEmploymentPositionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.getUnitEmploymentPosition(unitEmploymentPositionId));
    }

*/

//    @ApiOperation(value = "Get all positions by unit Employment")
//    @RequestMapping(value = "/positionCode")
//    ResponseEntity<Map<String, Object>> getAllUnitEmploymentPositions(@PathVariable Long unitEmploymentId) {
//        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.getAllUnitEmploymentPositions(unitEmploymentId));
//    }


}
