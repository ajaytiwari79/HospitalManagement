package com.kairos.controller.payroll_setting;

import com.kairos.dto.activity.payroll_setting.PayrollSettingDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.enums.DurationType;
import com.kairos.service.payroll_setting.PayrollSettingService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class PayrollSettingController {

   @Inject
   PayrollSettingService payrollSettingService;

    @ApiOperation(value = "Create PayRoll Period")
    @PostMapping(value="/payroll_period")
    public ResponseEntity<Map<String, Object>> createPayRollPeriod(@PathVariable Long unitId, @RequestBody PayrollSettingDTO payrollSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payrollSettingService.createPayrollPeriod(payrollSettingDTO,unitId));

    }

    @ApiOperation(value = "get year of PayRoll Period")
    @GetMapping(value="/payroll_period_dates")
    public ResponseEntity<Map<String, Object>> getLocalDatesOfPayRollPeriod(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payrollSettingService.getLocalDatesOfPayrollPeriod(unitId));

    }

    @ApiOperation(value = "get PayRoll Period")
    @GetMapping(value="/payroll_period")
    public ResponseEntity<Map<String, Object>> getPayRollPeriodOfUnit(@PathVariable Long unitId, @RequestParam(value = "year") Integer year, @RequestParam("durationType") DurationType durationType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payrollSettingService.getPayrollPeriodByUnitIdAndDateAndDurationType(unitId,year,durationType));

    }

    @ApiOperation(value = "update PayRoll Period")
    @PutMapping(value="/payroll_period")
    public ResponseEntity<Map<String, Object>> updatePayRollPeriod(@PathVariable Long unitId, @RequestBody  PayrollSettingDTO payrollSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payrollSettingService.updatePayrollPeriod(payrollSettingDTO,unitId));

    }

    @ApiOperation(value = "update old PayRoll Period and create new payroll period on break payroll table")
    @PutMapping(value="/break_payroll_period")
    public ResponseEntity<Map<String, Object>> breakPayRollPeriod(@PathVariable Long unitId, @RequestBody PayrollSettingDTO payrollSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payrollSettingService.breakPayrollPeriodOfUnit(unitId,payrollSettingDTO));

    }

    @ApiOperation(value = "update PayRoll Period")
    @DeleteMapping(value="/payroll_period")
    public ResponseEntity<Map<String, Object>> deleteDraftPayRollPeriod(@PathVariable Long unitId, @RequestBody  PayrollSettingDTO payrollSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payrollSettingService.deleteDraftPayrollPeriod(payrollSettingDTO,unitId));

    }
}
