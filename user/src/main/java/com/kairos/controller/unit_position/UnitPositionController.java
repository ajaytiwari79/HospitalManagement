package com.kairos.controller.unit_position;


import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.service.unit_position.UnitPositionService;
import com.kairos.dto.user.staff.unit_position.UnitPositionDTO;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ExecutionException;
import java.util.Set;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class UnitPositionController {

    @Inject
    private UnitPositionService unitPositionService;

    @ApiOperation(value = "Create a New Position")
    @PostMapping(value = "/unit_position")
    public ResponseEntity<Map<String, Object>> createUnitPosition(@PathVariable Long unitId, @RequestParam("type") String type, @RequestBody @Valid UnitPositionDTO position, @RequestParam("saveAsDraft") Boolean saveAsDraft)  throws InterruptedException ,ExecutionException     {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.createUnitPosition(unitId, type, position, false, saveAsDraft));
    }

    /*
     * @auth vipul
     * used to get all positions of organization n by organization and staff Id
     * */
    @ApiOperation(value = "Get all unit_position by organization and staff")
    @RequestMapping(value = "/unit_position/staff/{staffId}")
    ResponseEntity<Map<String, Object>> getUnitPositionsOfStaff(@PathVariable Long unitId, @RequestParam(value = "type",required = false) String type, @PathVariable Long staffId, @RequestParam("allOrganization") boolean allOrganization) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.getUnitPositionsOfStaff(unitId, staffId, allOrganization));
    }

    @ApiOperation(value = "Remove unit_position")
    @DeleteMapping(value = "/unit_position/{unitPositionId}")
    public ResponseEntity<Map<String, Object>> deleteUnitPosition(@PathVariable Long unitId, @PathVariable Long unitPositionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.removePosition(unitPositionId, unitId));
    }


    @ApiOperation(value = "Update unit_position")
    @PutMapping(value = "/unit_position/{unitPositionId}")
    public ResponseEntity<Map<String, Object>> updateUnitPosition(@PathVariable Long unitId, @PathVariable Long unitPositionId, @RequestBody @Valid UnitPositionDTO position, @RequestParam("type") String type, @RequestParam("saveAsDraft") Boolean saveAsDraft) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.updateUnitPosition(unitPositionId, position, unitId, type, saveAsDraft));
    }

    @ApiOperation(value = "Get unit_position")
    @GetMapping(value = "/unit_position/{unitPositionId}")
    public ResponseEntity<Map<String, Object>> getUnitPosition(@PathVariable Long unitId, @PathVariable Long unitPositionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.getUnitPosition(unitPositionId));
    }

    @ApiOperation(value = "Update unit_position's WTA")
    @PutMapping(value = "/unit_position/{unitPositionId}/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> updateUnitPositionWTA(@PathVariable Long unitPositionId, @PathVariable Long unitId, @PathVariable BigInteger wtaId, @RequestBody @Valid WTADTO wtadto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.updateUnitPositionWTA(unitId, unitPositionId, wtaId, wtadto));
    }

    @ApiOperation(value = "apply function to unit position")
    @PostMapping(value = "/unit_position/{unitPositionId}/applyFunction")
    public ResponseEntity<Map<String, Object>> applyFunction(@PathVariable Long unitPositionId,@PathVariable Long unitId, @RequestBody Map<String, Object> payload) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.applyFunction(unitPositionId, payload,unitId));
    }

    @ApiOperation(value = "apply function to unit position")
    @PostMapping(value = "/unit_position/{unitPositionId}/restore_functions")
    public ResponseEntity<Map<String, Object>> restoreFunctions(@PathVariable Long unitPositionId, @RequestBody Map<Long,Set<LocalDate>> payload)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.restoreFunctions(unitPositionId, payload));
    }

    @ApiOperation(value = "remove function to unit position")
    @DeleteMapping(value = "/unit_position/{unitPositionId}/applyFunction")
    public ResponseEntity<Map<String, Object>> removeFunction(@PathVariable Long unitPositionId, @RequestParam(value = "appliedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date appliedDate) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.removeFunction(unitPositionId, appliedDate));
    }

    /**
     *
     * @param unitPositionId
     * @param appliedDates
     * @return
     * @throws ParseException
     * @Desc this endpoint will remove applied functions for multiple dates
     */
    @ApiOperation(value = "remove function to unit position")
    @DeleteMapping(value = "/unit_position/{unitPositionId}/remove_functions")
    public ResponseEntity<Map<String, Object>> removeFunctions(@PathVariable Long unitPositionId, @RequestBody Set<LocalDate> appliedDates)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.removeFunctions(unitPositionId, appliedDates));
    }

    @ApiOperation(value = "get unit_position's Id By Staff and expertise")
    @GetMapping(value = "/staff/{staffId}/expertise/{expertiseId}/unitPositionId")
    public ResponseEntity<Map<String, Object>> getUnitPositionIdByStaffAndExpertise(@PathVariable Long unitId, @PathVariable Long staffId, @RequestParam(value = "dateInMillis") Long dateInMillis, @PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.getUnitPositionIdByStaffAndExpertise(unitId, staffId, dateInMillis, expertiseId));
    }

    @ApiOperation(value = "get unit positions based on expertise and staff list")
    @RequestMapping(value = "/expertise/{expertiseId}/unitPositions", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getStaffsUnitPosition(@PathVariable Long unitId, @PathVariable Long expertiseId, @RequestBody List<Long> staffList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.getStaffsUnitPosition(unitId, expertiseId, staffList));
    }

    @ApiOperation(value = "get unit positionsId based on expertise and staff list")
    @RequestMapping(value = "/expertise/{expertiseId}/staff_and_unit_positions", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getStaffIdAndUnitPositionId(@PathVariable Long unitId, @PathVariable Long expertiseId, @RequestBody List<Long> staffList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.getStaffIdAndUnitPositionId(unitId, expertiseId, staffList));
    }

    @ApiOperation(value = "get all wta version for a staff")
    @RequestMapping(value = "/staff/{staffId}/wta", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAOfStaff(@PathVariable Long unitId, @PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.getAllWTAOfStaff(unitId,staffId));
    }

    @ApiOperation(value = "get all cta version for a staff")
    @RequestMapping(value = "/staff/{staffId}/cta", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllCTAOfStaff(@PathVariable Long unitId, @PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.getAllCTAOfStaff(unitId, staffId));
    }

    //Do not remove, required for local testing.
   @ApiOperation(value = "update senioritylevel")
    @RequestMapping(value = "/seniority_level_update", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> updateSeniorityLevel() {
        unitPositionService.updateSeniorityLevelOnJobTrigger(new BigInteger("4"),999L);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }

    @ApiOperation(value = "get unit_position's CTA")
    @GetMapping(value = "/getCTAbyUnitPosition/{unitPositionId}")
    public ResponseEntity<Map<String, Object>> getUnitPositionCTA(@PathVariable Long unitPositionId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPositionService.getUnitPositionCTA(unitPositionId, unitId));
    }

}
