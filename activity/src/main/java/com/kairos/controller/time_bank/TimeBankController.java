package com.kairos.controller.time_bank;


import com.kairos.constants.ApiConstants;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping(ApiConstants.TIMEBANK_URL)
public class TimeBankController {


    @Autowired
    private TimeBankService timeBankService;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;

    @GetMapping(value = "/employment")
    public ResponseEntity<Map<String, Object>> getTimeBankForAdvanceView(@PathVariable Long unitId,@RequestParam(required = false) Long employmentId, @RequestParam(value = "query",required = false) String query, @RequestParam(value = "startDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @RequestParam(value = "endDate",required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeBankService.getAdvanceViewTimeBank
                (unitId,employmentId,query,startDate,endDate));
    }

    @GetMapping(value = "overview/employment/{employmentId}/")
    public ResponseEntity<Map<String, Object>> getTimeBankForOverview(@PathVariable Long unitId,@PathVariable Long employmentId, @RequestParam Integer year) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeBankService.getOverviewTimeBank
                (unitId,employmentId,year));
    }

    @GetMapping(value = "visual_view/employment/{employmentId}")
    public ResponseEntity<Map<String, Object>> getTimeBankForVisualView(@PathVariable Long unitId,@PathVariable Long employmentId,@RequestParam(value = "query",required = false) String query,@RequestParam(value = "value",required = false) Integer value,@RequestParam(value = "startDate",required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @RequestParam(value = "endDate",required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeBankService.getTimeBankForVisualView
                (unitId,employmentId,query,value,startDate,endDate));
    }

    @ApiOperation("Update time bank after applying function")
    @PutMapping("/update_time_bank")
    public ResponseEntity<Map<String,Object>> updateTimeBank(@RequestParam Long employmentId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date shiftStartDate, @RequestBody StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,timeBankService.updateTimeBankOnFunctionChange(shiftStartDate,staffAdditionalInfoDTO));
    }

    @ApiOperation("Update time bank after modification of employmentLine")
    @PutMapping("employment/{employmentId}/update_time_bank")
    public ResponseEntity<Map<String,Object>> updateTimeBankOnEmploymentModification(@RequestParam BigInteger ctaId, @PathVariable Long employmentId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date employmentLineStartDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date employmentLineEndDate, @RequestBody StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,timeBankService.updateTimeBankOnEmploymentModification(ctaId,employmentId,employmentLineStartDate,employmentLineEndDate,staffAdditionalInfoDTO));
    }


    @ApiOperation("Renew Timebank of Shifts")
    @PutMapping("/renew_timebank_shifts")
    public ResponseEntity<Map<String,Object>> renewTimebankOfShifts(){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,timeBankService.renewTimeBankOfShifts());
    }

    @ApiOperation("Remove duplicate Timebank Entryies")
    @DeleteMapping("/remove_duplicate_timebank")
    public ResponseEntity<Map<String,Object>> deleteDuplicateEntry(){
        timeBankService.deleteDuplicateEntry();
        return ResponseHandler.generateResponse(HttpStatus.OK,true,null);
    }


    //As discussed with Shiv kumar API name should be get_timebank_metadata
    @ApiOperation("Get accumulated timebank and delta timebank")
    @GetMapping("/get_timebank_metadata")
    public ResponseEntity<Map<String,Object>> getAccumulatedTimebankDTO(@PathVariable Long unitId,@RequestParam Long employmentId,@RequestParam(required = false) Boolean includeActualTimebank){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,timeBankService.getAccumulatedTimebankAndDelta(employmentId,unitId,includeActualTimebank));
    }

    //Todo remove this API after sprint 46 is closed
    @ApiOperation("Update Daily Timebank")
    @GetMapping("/update_daily_timebank")
    public ResponseEntity<Map<String,Object>> getAccumulatedTimebankDTO(@PathVariable Long unitId){
        timeBankService.updateDailyTimebank(unitId);
        return ResponseHandler.generateResponse(HttpStatus.OK,true,true);
    }

    @ApiOperation("Get CTARultemplate By EmploymentId")
    @GetMapping("/get_cta_rultemplate_by_employmentId")
    public ResponseEntity<Map<String,Object>> getCTARultemplateByEmploymentId(@PathVariable Long unitId,@RequestParam Long employmentId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,timeBankService.getCTARultemplateByEmploymentId(employmentId));
    }

    //remove after test qa
    @ApiOperation("update time bank of protected days off")
    @GetMapping("/test_timebank")
    public ResponseEntity<Map<String,Object>> testApiForProtecrtedDaysOFf(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,timeBankCalculationService.updateTimeBankAgainstProtectedDaysOffSetting());
    }


}
