package com.kairos.controller.pay_out;


import com.kairos.constants.ApiConstants;
import com.kairos.controller.task.TaskController;
import com.kairos.service.pay_out.PayOutService;
import com.kairos.utils.response.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping(ApiConstants.PAYOUT_URL)
public class PayOutController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private PayOutService payOutService;

    @PostMapping(value = "/{payoutTransactionId}")
    public ResponseEntity<Map<String, Object>> getPayOutForAdvanceView(@PathVariable BigInteger payoutTransactionId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payOutService.approvePayOutRequest(payoutTransactionId));
    }

    @PostMapping(value = "/request_payout/staff/{staffId}")
    public ResponseEntity<Map<String, Object>> requestPayOut(@PathVariable Long staffId,@RequestParam Long unitPositionId,@RequestParam int amount) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, payOutService.requestPayOut(staffId,unitPositionId,amount));
    }




}
