package com.kairos.controller.payroll;
/*
 *Created By Pavan on 17/12/18
 *
 */

import com.kairos.dto.activity.payroll.BankDTO;
import com.kairos.dto.activity.payroll.PayRollDTO;
import com.kairos.service.payroll.BankService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_V1;

@RestController
@RequestMapping(API_V1)
public class BankController {
    @Inject
    private BankService bankService;

    @ApiOperation("Create bank")
    @PostMapping(value = "/country/{countryId}/bank")
    public ResponseEntity<Map<String,Object>> createBank(@PathVariable Long countryId,@Valid @RequestBody BankDTO bankDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,bankService.createBank(countryId,bankDTO));
    }

    @ApiOperation("update bank")
    @PutMapping(value = "/country/{countryId}/bank/{bankId}")
    public ResponseEntity<Map<String,Object>> updateBank(@PathVariable BigInteger bankId,@PathVariable BigInteger countryId, @Valid @RequestBody BankDTO bankDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,bankService.updateBank(bankId,countryId,bankDTO));
    }

    @ApiOperation("delete PayRoll at System level")
    @DeleteMapping(value = "/payroll/{payRollId}")
    public ResponseEntity<Map<String,Object>> deletePayRoll(@PathVariable BigInteger payRollId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.deletePayRoll(payRollId));
    }

    @ApiOperation("get PayRoll at System level")
    @GetMapping(value = "/payroll/{payRollId}")
    public ResponseEntity<Map<String,Object>> getPayRollById(@PathVariable BigInteger payRollId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.getPayRollById(payRollId));
    }

    @ApiOperation("get All PayRoll at System level")
    @GetMapping(value = "/payroll")
    public ResponseEntity<Map<String,Object>> getAllPayRoll(){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.getAllPayRoll());
    }
}
