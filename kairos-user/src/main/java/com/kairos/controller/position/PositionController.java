package com.kairos.controller.position;


import com.kairos.response.dto.web.PositionDTO;
import com.kairos.service.position.PositionService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;
import org.slf4j.Logger;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class PositionController {


    @Inject
    private PositionService positionService;

    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    @ApiOperation(value = "Create a New Position")
  //  http://dev.kairosplanning.com/api/v1/organization/71/unit/71/unitEmployment/82/position?moduleId=tab_23&type=Organization
    @PostMapping(value = "/unitEmployment/{unitEmploymentId}/position")
    public ResponseEntity<Map<String, Object>> createPosition(@PathVariable Long organizationId,@PathVariable Long unitId,@PathVariable Long unitEmploymentId,
                                                              @RequestParam("type") String type,@RequestBody @Valid  PositionDTO position) {
       return ResponseHandler.generateResponse(HttpStatus.OK, true, positionService.createPosition(unitId,unitEmploymentId,position,type));
    }

    @ApiOperation(value = "Update Position")
    @PutMapping(value = "/position/{positionId}")
    public ResponseEntity<Map<String, Object>> updatePosition(@PathVariable Long positionId,@RequestBody @Valid  PositionDTO position) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, positionService.updatePosition(positionId,position));
    }

    @ApiOperation(value = "Get Position")
    @GetMapping(value = "/position/{positionId}")
    public ResponseEntity<Map<String, Object>> getPosition(@PathVariable Long positionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, positionService.getPosition(positionId));
    }

    @ApiOperation(value = "Remove Position")
    @DeleteMapping(value = "/position/{positionId}")
    public ResponseEntity<Map<String, Object>> deletePosition(@PathVariable Long positionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, positionService.removePosition(positionId));
    }
    @ApiOperation(value = "Get all positions by unit Employment")
    @RequestMapping(value="/unitEmployment/{unitEmploymentId}/position")
    ResponseEntity<Map<String, Object>> getAllPositions(@PathVariable Long unitEmploymentId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,positionService.getAllPositions(unitEmploymentId));
    }
    /*
    * @auth vipul
    * used to get all positions of organization n by organization and staff Id
    * */
    @ApiOperation(value = "Get all positions by organization and staff")
    @RequestMapping(value="/unitEmployment/{unitEmploymentId}/staff/{staffId}/position")
    ResponseEntity<Map<String, Object>> getAllPositionByStaff(@PathVariable Long unitId,@RequestParam("type") String type,@PathVariable Long unitEmploymentId,@PathVariable Long staffId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,positionService.getAllPositionByStaff(unitId,unitEmploymentId,staffId,type));
    }


}
