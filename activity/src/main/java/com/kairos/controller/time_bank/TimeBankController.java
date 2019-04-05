package com.kairos.controller.time_bank;


import com.kairos.constants.ApiConstants;
import com.kairos.controller.task.TaskController;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping(ApiConstants.TIMEBANK_URL)
public class TimeBankController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TimeBankService timeBankService;

    /*@RequestMapping(value = "/createTimeBank", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createTimeBank(TimeBankDTO timeBankDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeBankService.createTimeBank(timeBankDTO));
    }

    @RequestMapping(value = "/updateTimeBank", method = RequestMethod.PUT)
    public ResponseEntity<Map<String, Object>> updateTimeBank(@RequestBody TimeBankDTO timeBankDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeBankService.updateTimeBankAfterCheckoutShift(timeBankDTO));
    }

    @RequestMapping(value = "/getTimeBank", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getTimeBank(Long unitEmpPositionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeBankService.getTimeBank(unitEmpPositionId));
    }

    */

    @GetMapping(value = "/unit_position/{unitPositionId}/")
    public ResponseEntity<Map<String, Object>> getTimeBankForAdvanceView(@PathVariable Long unitId,@PathVariable Long unitPositionId, @RequestParam(value = "query") String query, @RequestParam(value = "startDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @RequestParam(value = "endDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeBankService.getAdvanceViewTimeBank
                (unitId,unitPositionId,query,startDate,endDate));
    }

    @GetMapping(value = "overview/unit_position/{unitPositionId}/")
    public ResponseEntity<Map<String, Object>> getTimeBankForOverview(@PathVariable Long unitId,@PathVariable Long unitPositionId, @RequestParam Integer year) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeBankService.getOverviewTimeBank
                (unitId,unitPositionId,year));
    }

    @GetMapping(value = "visual_view/unit_position/{unitPositionId}")
    public ResponseEntity<Map<String, Object>> getTimeBankForVisualView(@PathVariable Long unitId,@PathVariable Long unitPositionId,@RequestParam(value = "query",required = false) String query,@RequestParam(value = "value",required = false) Integer value,@RequestParam(value = "startDate",required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @RequestParam(value = "endDate",required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeBankService.getTimeBankForVisualView
                (unitId,unitPositionId,query,value,startDate,endDate));
    }

    @ApiOperation("Update time bank after applying function")
    @PutMapping("/update_time_bank")
    public ResponseEntity<Map<String,Object>> updateTimeBank(@RequestParam Long unitPositionId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date shiftStartDate, @RequestBody StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,timeBankService.updateTimeBankOnFunctionChange(shiftStartDate,staffAdditionalInfoDTO));
    }

    @ApiOperation("Update time bank after modification of employmentLine")
    @PutMapping("unit_position/{unitPositionId}/update_time_bank")
    public ResponseEntity<Map<String,Object>> updateTimeBankOnUnitPositionModification(@RequestParam BigInteger ctaId,@PathVariable Long unitPositionId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date employmentLineStartDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date employmentLineEndDate, @RequestBody StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,timeBankService.updateTimeBankOnUnitPositionModification(ctaId,unitPositionId,employmentLineStartDate,employmentLineEndDate,staffAdditionalInfoDTO));
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
    public ResponseEntity<Map<String,Object>> getAccumulatedTimebankDTO(@PathVariable Long unitId,@RequestParam Long unitPositionId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,timeBankService.getAccumulatedTimebankAndDeltaDTO(unitPositionId,unitId,startDate,endDate));
    }


}
