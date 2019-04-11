package com.kairos.controller.unit_position;


import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.service.organization.UnionService;
import com.kairos.service.unit_position.EmploymentCTAWTAService;
import com.kairos.service.unit_position.EmploymentFunctionService;
import com.kairos.service.unit_position.EmploymentJobService;
import com.kairos.service.unit_position.EmploymentService;
import com.kairos.dto.user.staff.unit_position.EmploymentDTO;
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

import java.util.Set;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class EmploymentController {

    @Inject
    private EmploymentService employmentService;
    @Inject
    private EmploymentJobService employmentJobService;
    @Inject private EmploymentFunctionService employmentFunctionService;
    @Inject private EmploymentCTAWTAService employmentCTAWTAService;
    @Inject private UnionService unionService;

    @ApiOperation(value = "Create a New Position")
    @PostMapping(value = "/unit_position")
    public ResponseEntity<Map<String, Object>> createUnitPosition(@PathVariable Long unitId, @RequestParam("type") String type, @RequestBody @Valid EmploymentDTO position, @RequestParam("saveAsDraft") Boolean saveAsDraft) throws Exception {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.createEmployment(unitId, type, position, false, saveAsDraft));
    }

    /*
     * @auth vipul
     * used to get all positions of organization n by organization and staff Id
     * */
    @ApiOperation(value = "Get all unit_position by organization and staff")
    @RequestMapping(value = "/unit_position/staff/{staffId}")
    ResponseEntity<Map<String, Object>> getUnitPositionsOfStaff(@PathVariable Long unitId, @RequestParam(value = "type",required = false) String type, @PathVariable Long staffId, @RequestParam("allOrganization") boolean allOrganization) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getEmploymentsOfStaff(unitId, staffId, allOrganization));
    }

    @ApiOperation(value = "Remove unit_position")
    @DeleteMapping(value = "/unit_position/{unitPositionId}")
    public ResponseEntity<Map<String, Object>> deleteUnitPosition(@PathVariable Long unitId, @PathVariable Long unitPositionId) throws Exception {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.removePosition(unitPositionId, unitId));
    }


    @ApiOperation(value = "Update unit_position")
    @PutMapping(value = "/unit_position/{unitPositionId}")
    public ResponseEntity<Map<String, Object>> updateUnitPosition(@PathVariable Long unitId, @PathVariable Long unitPositionId, @RequestBody @Valid EmploymentDTO position, @RequestParam("type") String type, @RequestParam("saveAsDraft") Boolean saveAsDraft) throws Exception {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.updateEmployment(unitPositionId, position, unitId, type, saveAsDraft));
    }

    @ApiOperation(value = "Get unit_position")
    @GetMapping(value = "/unit_position/{unitPositionId}")
    public ResponseEntity<Map<String, Object>> getUnitPosition(@PathVariable Long unitId, @PathVariable Long unitPositionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getEmployment(unitPositionId));
    }

    @ApiOperation(value = "Update unit_position's WTA")
    @PutMapping(value = "/unit_position/{unitPositionId}/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> updateUnitPositionWTA(@PathVariable Long unitPositionId, @PathVariable Long unitId, @PathVariable BigInteger wtaId, @RequestBody @Valid WTADTO wtadto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentCTAWTAService.updateEmploymentWTA(unitId, unitPositionId, wtaId, wtadto));
    }

    @ApiOperation(value = "apply function to unit position")
    @PostMapping(value = "/unit_position/{unitPositionId}/applyFunction")
    public ResponseEntity<Map<String, Object>> applyFunction(@PathVariable Long unitPositionId,@PathVariable Long unitId, @RequestBody Map<String, Long> payload) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentFunctionService.applyFunction(unitPositionId, payload,unitId));
    }

    @ApiOperation(value = "apply function to unit position")
    @PostMapping(value = "/unit_position/{unitPositionId}/restore_functions")
    public ResponseEntity<Map<String, Object>> restoreFunctions(@PathVariable Long unitPositionId, @RequestBody Map<Long,Set<LocalDate>> payload)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentFunctionService.restoreFunctions(unitPositionId, payload));
    }

    @ApiOperation(value = "remove function to unit position")
    @DeleteMapping(value = "/unit_position/{unitPositionId}/applyFunction")
    public ResponseEntity<Map<String, Object>> removeFunction(@PathVariable Long unitId,@PathVariable Long unitPositionId, @RequestParam(value = "appliedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date appliedDate) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentFunctionService.removeFunction(unitId,unitPositionId, appliedDate));
    }

    @ApiOperation(value = "remove function to unit position on delete Shift")
    @DeleteMapping(value = "/unit_position/{unitPositionId}/remove_function_on_delete_shift")
    public ResponseEntity<Map<String, Object>> removeFunctionOnDeleteShift(@PathVariable Long unitId,@PathVariable Long unitPositionId, @RequestParam(value = "appliedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date appliedDate) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentFunctionService.removeFunctionOnDeleteShift(unitPositionId, appliedDate));
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
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentFunctionService.removeFunctions(unitPositionId, appliedDates));
    }

    @ApiOperation(value = "get employment's Id By Staff and expertise")
    @GetMapping(value = "/staff/{staffId}/expertise/{expertiseId}/employmentId")
    public ResponseEntity<Map<String, Object>> getUnitPositionIdByStaffAndExpertise(@PathVariable Long unitId, @PathVariable Long staffId, @PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getEmploymentIdByStaffAndExpertise(unitId, staffId, expertiseId));
    }

    @ApiOperation(value = "get unit positions based on expertise and staff list")
    @RequestMapping(value = "/expertise/{expertiseId}/employments", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getStaffsUnitPosition(@PathVariable Long unitId, @PathVariable Long expertiseId, @RequestBody List<Long> staffList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getStaffsEmployment(unitId, expertiseId, staffList));
    }

    @ApiOperation(value = "get unit positionsId based on expertise and staff list")
    @RequestMapping(value = "/expertise/{expertiseId}/staff_and_employments", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getStaffIdAndUnitPositionId(@PathVariable Long unitId, @PathVariable Long expertiseId, @RequestBody List<Long> staffList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getStaffIdAndEmploymentId(unitId, expertiseId, staffList));
    }

    @ApiOperation(value = "get all wta version for a staff")
    @RequestMapping(value = "/staff/{staffId}/wta", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllWTAOfStaff(@PathVariable Long unitId, @PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentCTAWTAService.getAllWTAOfStaff(unitId,staffId));
    }

    @ApiOperation(value = "get all cta version for a staff")
    @RequestMapping(value = "/staff/{staffId}/cta", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getAllCTAOfStaff(@PathVariable Long unitId, @PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentCTAWTAService.getAllCTAOfStaff(unitId, staffId));
    }

    //Do not remove, required for local testing.
   @ApiOperation(value = "update senioritylevel")
    @RequestMapping(value = "/seniority_level_update", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> updateSeniorityLevel() {
        employmentJobService.updateSeniorityLevelOnJobTrigger(new BigInteger("4"),999L);
        return ResponseHandler.generateResponse(HttpStatus.OK, true,null);
    }

    @ApiOperation(value = "get employment's CTA")
    @GetMapping(value = "/cta_by_employment/{employmentId}")
    public ResponseEntity<Map<String, Object>> getUnitPositionCTA(@PathVariable Long unitPositionId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentCTAWTAService.getEmploymentCTA(unitPositionId, unitId));
    }


    @ApiOperation(value = "get UnitPositions Per Staff")
    @GetMapping(value = "/staff/{staffId}/unit_positions")
    public ResponseEntity<Map<String, Object>> getUnitPositionsByStaffId(@PathVariable Long unitId, @PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getEmploymentsByStaffId(unitId, staffId));
    }

    @ApiOperation(value = "get HourlyCost By employmentLine Wise")
    @GetMapping(value = "/staff/{staffId}/unit_positions/{unitPositionId}/hourly_cost")
    public ResponseEntity<Map<String, Object>> getEmploymentLinesWithHourlyCost(@PathVariable Long unitId, @PathVariable Long staffId,@PathVariable Long unitPositionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentFunctionService.getEmploymentLinesWithHourlyCost(unitId, staffId,unitPositionId));
    }

    @RequestMapping(value = "/unit_position/default_data", method = RequestMethod.GET)
    @ApiOperation("Get All default data for unit employment  by organization ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getUnitPositionDefaultData(@PathVariable Long unitId, @RequestParam("type") String type, @RequestParam("staffId") Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                unionService.getEmploymentDefaultData(unitId, type, staffId));
    }


    @ApiOperation(value = "get unit By unit position")
    @GetMapping(value = "/unit_position/{unitPositionId}/get_unit")
    public ResponseEntity<Map<String, Object>> removeFunctions(@PathVariable Long unitPositionId)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getUnitByEmploymentId(unitPositionId));
    }

    @GetMapping("/employment/expertise")
    @ApiOperation("fetch Map of unit position id and expertise id")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertiseOfUnitPosition(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getEmploymentExpertiseMap(unitId));
    }
}
