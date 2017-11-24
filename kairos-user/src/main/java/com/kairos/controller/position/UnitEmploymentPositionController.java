package com.kairos.controller.position;


import com.kairos.response.dto.web.UnitEmploymentPositionDTO;
import com.kairos.service.position.UnitEmploymentPositionService;
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
@RequestMapping(API_ORGANIZATION_UNIT_URL+"/unitEmployment/{unitEmploymentId}")
@Api(API_ORGANIZATION_UNIT_URL+"/unitEmployment/{unitEmploymentId}")
public class UnitEmploymentPositionController {


    @Inject
    private UnitEmploymentPositionService unitEmploymentPositionService;

    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    @ApiOperation(value = "Create a New Position")
  //  http://dev.kairosplanning.com/api/v1/organization/71/unit/71/unitEmployment/82/position?moduleId=tab_23&type=Organization
    @PostMapping(value = "/position")
    public ResponseEntity<Map<String, Object>> createUnitEmploymentPosition(@PathVariable Long organizationId, @PathVariable Long unitId, @PathVariable Long unitEmploymentId,
                                                                            @RequestParam("type") String type, @RequestBody @Valid UnitEmploymentPositionDTO position) {
       return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.createUnitEmploymentPosition(unitId,unitEmploymentId,position,type));
    }

    @ApiOperation(value = "Update Position")
    @PutMapping(value = "/position/{positionId}")
    public ResponseEntity<Map<String, Object>> createUnitEmploymentPosition(@PathVariable Long positionId,@RequestBody @Valid UnitEmploymentPositionDTO position) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.updateUnitEmploymentPosition(positionId,position));
    }

    @ApiOperation(value = "Get Position")
    @GetMapping(value = "/position/{positionId}")
    public ResponseEntity<Map<String, Object>> getUnitEmploymentPosition(@PathVariable Long positionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.getUnitEmploymentPosition(positionId));
    }


    @ApiOperation(value = "Remove Position")
    @DeleteMapping(value = "/position/{positionId}")
    public ResponseEntity<Map<String, Object>> deleteUnitEmploymentPosition(@PathVariable Long positionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.removePosition(positionId));
    }
    @ApiOperation(value = "Get all positions by unit Employment")
    @RequestMapping(value="/position")
    ResponseEntity<Map<String, Object>> getAllUnitEmploymentPositions(@PathVariable Long unitEmploymentId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true, unitEmploymentPositionService.getAllUnitEmploymentPositions(unitEmploymentId));
    }
    /*
    * @auth vipul
    * used to get all positions of organization n by organization and staff Id
    * */
    @ApiOperation(value = "Get all positions by organization and staff")
    @RequestMapping(value="/staff/{staffId}/position")
    ResponseEntity<Map<String, Object>> getAlllUnitEmploymentPositionsOfStaff(@PathVariable Long unitId,@RequestParam("type") String type,@PathVariable Long unitEmploymentId,@PathVariable Long staffId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true, unitEmploymentPositionService.getAlllUnitEmploymentPositionsOfStaff(unitId,unitEmploymentId,staffId,type));
    }


}
