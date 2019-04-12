package com.kairos.controller.employment;


import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.service.organization.UnionService;
import com.kairos.service.employment.EmploymentCTAWTAService;
import com.kairos.service.employment.EmploymentFunctionService;
import com.kairos.service.employment.EmploymentJobService;
import com.kairos.service.employment.EmploymentService;
import com.kairos.dto.user.staff.employment.EmploymentDTO;
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
    @PostMapping(value = "/employment")
    public ResponseEntity<Map<String, Object>> createEmployment(@PathVariable Long unitId, @RequestParam("type") String type, @RequestBody @Valid EmploymentDTO position, @RequestParam("saveAsDraft") Boolean saveAsDraft) throws Exception {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.createEmployment(unitId, type, position, false, saveAsDraft));
    }

    /*
     * @auth vipul
     * used to get all positions of organization n by organization and staff Id
     * */
    @ApiOperation(value = "Get all employment by organization and staff")
    @RequestMapping(value = "/employment/staff/{staffId}")
    ResponseEntity<Map<String, Object>> getEmploymentsOfStaff(@PathVariable Long unitId, @RequestParam(value = "type",required = false) String type, @PathVariable Long staffId, @RequestParam("allOrganization") boolean allOrganization) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getEmploymentsOfStaff(unitId, staffId, allOrganization));
    }

    @ApiOperation(value = "Remove employment")
    @DeleteMapping(value = "/employment/{employmentId}")
    public ResponseEntity<Map<String, Object>> deleteEmployment(@PathVariable Long unitId, @PathVariable Long employmentId) throws Exception {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.removePosition(employmentId, unitId));
    }


    @ApiOperation(value = "Update employment")
    @PutMapping(value = "/employment/{employmentId}")
    public ResponseEntity<Map<String, Object>> updateEmployment(@PathVariable Long unitId, @PathVariable Long employmentId, @RequestBody @Valid EmploymentDTO position, @RequestParam("type") String type, @RequestParam("saveAsDraft") Boolean saveAsDraft) throws Exception {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.updateEmployment(employmentId, position, unitId, type, saveAsDraft));
    }

    @ApiOperation(value = "Get employment")
    @GetMapping(value = "/employment/{employmentId}")
    public ResponseEntity<Map<String, Object>> getEmployment(@PathVariable Long unitId, @PathVariable Long employmentId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getEmployment(employmentId));
    }

    @ApiOperation(value = "Update employment's WTA")
    @PutMapping(value = "/employment/{employmentId}/wta/{wtaId}")
    public ResponseEntity<Map<String, Object>> updateEmploymentWTA(@PathVariable Long employmentId, @PathVariable Long unitId, @PathVariable BigInteger wtaId, @RequestBody @Valid WTADTO wtadto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentCTAWTAService.updateEmploymentWTA(unitId, employmentId, wtaId, wtadto));
    }

    @ApiOperation(value = "apply function to employment")
    @PostMapping(value = "/employment/{employmentId}/applyFunction")
    public ResponseEntity<Map<String, Object>> applyFunction(@PathVariable Long employmentId,@PathVariable Long unitId, @RequestBody Map<String, Long> payload) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentFunctionService.applyFunction(employmentId, payload,unitId));
    }

    @ApiOperation(value = "apply function to employment")
    @PostMapping(value = "/employment/{employmentId}/restore_functions")
    public ResponseEntity<Map<String, Object>> restoreFunctions(@PathVariable Long employmentId, @RequestBody Map<Long,Set<LocalDate>> payload)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentFunctionService.restoreFunctions(employmentId, payload));
    }

    @ApiOperation(value = "remove function to employment")
    @DeleteMapping(value = "/employment/{employmentId}/applyFunction")
    public ResponseEntity<Map<String, Object>> removeFunction(@PathVariable Long unitId,@PathVariable Long employmentId, @RequestParam(value = "appliedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date appliedDate) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentFunctionService.removeFunction(unitId,employmentId, appliedDate));
    }

    @ApiOperation(value = "remove function to employment on delete Shift")
    @DeleteMapping(value = "/employment/{employmentId}/remove_function_on_delete_shift")
    public ResponseEntity<Map<String, Object>> removeFunctionOnDeleteShift(@PathVariable Long unitId,@PathVariable Long employmentId, @RequestParam(value = "appliedDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date appliedDate) throws ParseException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentFunctionService.removeFunctionOnDeleteShift(employmentId, appliedDate));
    }

    /**
     *
     * @param employmentId
     * @param appliedDates
     * @return
     * @throws ParseException
     * @Desc this endpoint will remove applied functions for multiple dates
     */
    @ApiOperation(value = "remove function to employment")
    @DeleteMapping(value = "/employment/{employmentId}/remove_functions")
    public ResponseEntity<Map<String, Object>> removeFunctions(@PathVariable Long employmentId, @RequestBody Set<LocalDate> appliedDates)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentFunctionService.removeFunctions(employmentId, appliedDates));
    }

    @ApiOperation(value = "get employment's Id By Staff and expertise")
    @GetMapping(value = "/staff/{staffId}/expertise/{expertiseId}/employmentId")
    public ResponseEntity<Map<String, Object>> getEmploymentIdByStaffAndExpertise(@PathVariable Long unitId, @PathVariable Long staffId, @PathVariable Long expertiseId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getEmploymentIdByStaffAndExpertise(unitId, staffId, expertiseId));
    }

    @ApiOperation(value = "get employments based on expertise and staff list")
    @RequestMapping(value = "/expertise/{expertiseId}/employments", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getStaffsEmployment(@PathVariable Long unitId, @PathVariable Long expertiseId, @RequestBody List<Long> staffList) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getStaffsEmployment(unitId, expertiseId, staffList));
    }

    @ApiOperation(value = "get employmentsId based on expertise and staff list")
    @RequestMapping(value = "/expertise/{expertiseId}/staff_and_employments", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getStaffIdAndEmploymentId(@PathVariable Long unitId, @PathVariable Long expertiseId, @RequestBody List<Long> staffList) {
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
    public ResponseEntity<Map<String, Object>> getEmploymentCTA(@PathVariable Long employmentId, @PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentCTAWTAService.getEmploymentCTA(employmentId, unitId));
    }


    @ApiOperation(value = "get employments Per Staff")
    @GetMapping(value = "/staff/{staffId}/employments")
    public ResponseEntity<Map<String, Object>> getEmploymentsByStaffId(@PathVariable Long unitId, @PathVariable Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getEmploymentsByStaffId(unitId, staffId));
    }

    @ApiOperation(value = "get HourlyCost By employmentLine Wise")
    @GetMapping(value = "/staff/{staffId}/employments/{employmentId}/hourly_cost")
    public ResponseEntity<Map<String, Object>> getEmploymentLinesWithHourlyCost(@PathVariable Long unitId, @PathVariable Long staffId,@PathVariable Long employmentId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentFunctionService.getEmploymentLinesWithHourlyCost(unitId, staffId,employmentId));
    }

    @RequestMapping(value = "/employment/default_data", method = RequestMethod.GET)
    @ApiOperation("Get All default data for unit employment  by organization ")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getEmploymentDefaultData(@PathVariable Long unitId, @RequestParam("type") String type, @RequestParam("staffId") Long staffId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,
                unionService.getEmploymentDefaultData(unitId, type, staffId));
    }


    @ApiOperation(value = "get unit By employment")
    @GetMapping(value = "/employment/{employmentId}/get_unit")
    public ResponseEntity<Map<String, Object>> removeFunctions(@PathVariable Long employmentId)  {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getUnitByEmploymentId(employmentId));
    }

    @GetMapping("/employment/expertise")
    @ApiOperation("fetch Map of employment id and expertise id")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getExpertiseOfEmployment(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, employmentService.getEmploymentExpertiseMap(unitId));
    }
}
