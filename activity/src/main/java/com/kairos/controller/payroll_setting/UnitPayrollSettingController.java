package com.kairos.controller.payroll_setting;

import com.kairos.dto.activity.payroll_setting.UnitPayrollSettingDTO;
import com.kairos.enums.payroll_setting.PayrollFrequency;
import com.kairos.service.payroll_setting.UnitPayrollSettingService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;

@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class UnitPayrollSettingController {

   @Inject
   UnitPayrollSettingService unitPayrollSettingService;

    @ApiOperation(value = "Create PayRoll Period")
    @PostMapping(value="/payroll_period")
    public ResponseEntity<Map<String, Object>> createPayRollPeriod(@PathVariable Long unitId, @RequestBody UnitPayrollSettingDTO unitPayrollSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPayrollSettingService.createPayrollPeriod(unitPayrollSettingDTO,unitId));

    }

    @ApiOperation(value = "get year of PayRoll Period")
    @GetMapping(value="/available_payroll_periods")
    public ResponseEntity<Map<String, Object>> getLocalDatesOfPayRollPeriod(@PathVariable Long unitId,@RequestParam PayrollFrequency payrollFrequency) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPayrollSettingService.getDefaultDataOfPayrollPeriod(unitId,payrollFrequency));

    }

    @ApiOperation(value = "get PayRoll Period")
    @GetMapping(value="/payroll_period")
    public ResponseEntity<Map<String, Object>> getPayRollPeriodOfUnit(@PathVariable Long unitId, @RequestParam(value = "year") Integer year, @RequestParam PayrollFrequency payrollFrequency) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPayrollSettingService.getPayrollPeriodByUnitIdAndDateAndDurationType(unitId,year,payrollFrequency));

    }


    @ApiOperation(value = "update PayRoll Period")
    @PutMapping(value="/payroll_period")
    public ResponseEntity<Map<String, Object>> updatePayRollPeriod(@PathVariable Long unitId, @RequestBody UnitPayrollSettingDTO unitPayrollSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPayrollSettingService.updatePayrollPeriod(unitPayrollSettingDTO,unitId));

    }

    @ApiOperation(value = "update old PayRoll Period and create new payroll period on break payroll table")
    @PostMapping(value="/break_payroll_period")
    public ResponseEntity<Map<String, Object>> breakPayRollPeriod(@PathVariable Long unitId, @RequestBody UnitPayrollSettingDTO unitPayrollSettingDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPayrollSettingService.breakPayrollPeriodOfUnit(unitId, unitPayrollSettingDTO));

    }

    @ApiOperation(value = "delete PayRoll Period")
    @DeleteMapping(value="/payroll_period/{payrollPeriodId}")
    public ResponseEntity<Map<String, Object>> deleteDraftPayRollPeriod(@PathVariable Long unitId, @PathVariable BigInteger payrollPeriodId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, unitPayrollSettingService.deleteDraftPayrollPeriod(payrollPeriodId,unitId));

    }
}
