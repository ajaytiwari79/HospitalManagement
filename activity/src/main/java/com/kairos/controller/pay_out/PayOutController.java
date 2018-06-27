package com.kairos.controller.pay_out;


import com.kairos.constants.ApiConstants;
import com.kairos.controller.task.TaskController;
import com.kairos.response.dto.pay_out.UnitPositionWithCtaDetailsDTO;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.util.response.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping(ApiConstants.PAYOUT_URL)
public class PayOutController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private PayOutService payOutService;

    @GetMapping(value = "/unit_position/{unitEmploymentId}/")
    public ResponseEntity<Map<String, Object>> getPayOutForAdvanceView(@PathVariable Long unitId, @PathVariable Long unitEmploymentId, @RequestParam(value = "query") String query, @RequestParam(value = "startDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @RequestParam(value = "endDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payOutService.getAdvanceViewPayOut
                (unitId,unitEmploymentId,query,startDate,endDate));
    }

    @GetMapping(value = "overview/unit_position/{unitEmploymentId}/")
    public ResponseEntity<Map<String, Object>> getPayOutForOverview(@PathVariable Long unitId, @PathVariable Long unitEmploymentId, @RequestParam Integer year) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payOutService.getOverviewPayOut
                (unitEmploymentId,year));
    }


    @RequestMapping(value = "/savePayOut", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getPayOutIncludedTimeTypes() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payOutService.savePayOut());
    }

    @PutMapping(value = "/updateBlankPayOut")
    public ResponseEntity<Map<String,Object>> updateBlankPayOut(@RequestBody UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payOutService.updateBlankPayOut(unitPositionWithCtaDetailsDTO));
    }


}
