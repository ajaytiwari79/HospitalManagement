package com.kairos.activity.controller.timeBank;


import com.kairos.activity.constants.ApiConstants;
import com.kairos.activity.controller.task.TaskController;
import com.kairos.activity.response.dto.time_bank.TimebankWrapper;
import com.kairos.activity.service.time_bank.TimeBankService;
import com.kairos.activity.util.response.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @GetMapping(value = "/unit_position/{unitEmploymentId}/")
    public ResponseEntity<Map<String, Object>> getTimeBankForAdvanceView(@PathVariable Long unitId,@PathVariable Long unitEmploymentId, @RequestParam(value = "query") String query, @RequestParam(value = "startDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @RequestParam(value = "endDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeBankService.getAdvanceViewTimeBank
                (unitId,unitEmploymentId,query,startDate,endDate));
    }

    @GetMapping(value = "overview/unit_position/{unitEmploymentId}/")
    public ResponseEntity<Map<String, Object>> getTimeBankForOverview(@PathVariable Long unitId,@PathVariable Long unitEmploymentId, @RequestParam Integer year) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeBankService.getOverviewTimeBank
                (unitId,unitEmploymentId,year));
    }


    @RequestMapping(value = "/saveTimeBank", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getTimeBankIncludedTimeTypes() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, timeBankService.saveTimeBank());
    }

    @PostMapping(value = "/createBlankTimebank")
    public ResponseEntity<Map<String,Object>> createBlankTimeBank(@RequestBody TimebankWrapper timebankWrapper){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,timeBankService.createBlankTimeBank(timebankWrapper));
    }

    @PutMapping(value = "/updateBlankTimebank")
    public ResponseEntity<Map<String,Object>> updateBlankTimebank(@RequestBody TimebankWrapper timebankWrapper){
        return ResponseHandler.generateResponse(HttpStatus.OK, true,timeBankService.updateBlankTimebank(timebankWrapper));
    }


}
