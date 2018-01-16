package com.kairos.controller.position;


import com.kairos.persistence.model.user.agreement.wta.WTADTO;
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
@RequestMapping(API_ORGANIZATION_UNIT_URL + "/unitEmployment/{unitEmploymentId}")
@Api(API_ORGANIZATION_UNIT_URL + "/unitEmployment/{unitEmploymentId}")
public class UnitEmploymentPositionController {

    @Inject
    private UnitEmploymentPositionService unitEmploymentPositionService;

    @ApiOperation(value = "Create a New Position")
    //  http://dev.kairosplanning.com/api/v1/organization/71/unit/71/unitEmployment/82/position?moduleId=tab_23&type=Organization
    @PostMapping(value = "/position")
    public ResponseEntity<Map<String, Object>> createUnitEmploymentPosition(@PathVariable Long organizationId, @PathVariable Long unitId, @PathVariable Long unitEmploymentId,
                                                                            @RequestParam("type") String type, @RequestBody @Valid UnitEmploymentPositionDTO position) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.createUnitEmploymentPosition(unitId, unitEmploymentId, position, type));
    }

    @ApiOperation(value = "Update Position")
    @PutMapping(value = "/position/{unitEmploymentPositionId}")
    public ResponseEntity<Map<String, Object>> updateUnitEmploymentPosition(@PathVariable Long unitEmploymentPositionId, @RequestBody @Valid UnitEmploymentPositionDTO position) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.updateUnitEmploymentPosition(unitEmploymentPositionId, position));
    }

    @ApiOperation(value = "Update Position's WTA")
    @PutMapping(value = "/position/{unitEmploymentPositionId}/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> updateUnitEmploymentPositionWTA(@PathVariable Long unitEmploymentPositionId, @PathVariable Long unitId, @PathVariable Long wtaId, @RequestBody @Valid WTADTO wtadto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.updateUnitEmploymentPositionWTA(unitId, unitEmploymentPositionId, wtaId, wtadto));
    }

    @ApiOperation(value = "Get cta and wta by expertise for position creation")
    @RequestMapping(value="/position/expertise/{expertiseId}/cta_wta")
    ResponseEntity<Map<String,Object>> getCtaAndWtaByExpertiseId(@PathVariable Long unitId,@PathVariable Long expertiseId){

        return ResponseHandler.generateResponse(HttpStatus.OK,true,unitEmploymentPositionService.getCtaAndWtaByExpertiseId(unitId,expertiseId));
    }

    @ApiOperation(value = "get Position's WTA")
    @GetMapping(value = "/position/{unitEmploymentPositionId}/wta")
    public ResponseEntity<Map<String, Object>> getUnitEmploymentPositionWTA(@PathVariable Long unitEmploymentPositionId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.getUnitEmploymentPositionWTA(unitId, unitEmploymentPositionId));
    }

    @ApiOperation(value = "Get Position")
    @GetMapping(value = "/position/{unitEmploymentPositionId}")
    public ResponseEntity<Map<String, Object>> getUnitEmploymentPosition(@PathVariable Long unitEmploymentPositionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.getUnitEmploymentPosition(unitEmploymentPositionId));
    }


    @ApiOperation(value = "Remove Position")
    @DeleteMapping(value = "/position/{unitEmploymentPositionId}")
    public ResponseEntity<Map<String, Object>> deleteUnitEmploymentPosition(@PathVariable Long unitEmploymentPositionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.removePosition(unitEmploymentPositionId));
    }

    @ApiOperation(value = "Get all positions by unit Employment")
    @RequestMapping(value = "/position")
    ResponseEntity<Map<String, Object>> getAllUnitEmploymentPositions(@PathVariable Long unitEmploymentId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.getAllUnitEmploymentPositions(unitEmploymentId));
    }

    /*
    * @auth vipul
    * used to get all positions of organization n by organization and staff Id
    * */
    @ApiOperation(value = "Get all positions by organization and staff")
    @RequestMapping(value = "/staff/{staffId}/position")
    ResponseEntity<Map<String, Object>> getAllUnitEmploymentPositionsOfStaff(@PathVariable Long unitId, @RequestParam("type") String type, @PathVariable Long unitEmploymentId, @PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitEmploymentPositionService.getAllUnitEmploymentPositionsOfStaff(unitId, unitEmploymentId, staffId, type));
    }


}
