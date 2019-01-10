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
import static com.kairos.constants.ApiConstants.COUNTRY_URL;
import static com.kairos.constants.payroll.PayRollConstants.*;

@RestController
@RequestMapping(API_V1)
public class BankController {
    @Inject
    private BankService bankService;

    @ApiOperation("Create bank")
    @PostMapping(COUNTRY_URL+BANK)
    public ResponseEntity<Map<String,Object>> createBank(@PathVariable Long countryId,@Valid @RequestBody BankDTO bankDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,bankService.createBank(countryId,bankDTO));
    }

    @ApiOperation("update bank")
    @PutMapping(COUNTRY_URL+UPDATE_BANK)
    public ResponseEntity<Map<String,Object>> updateBank(@PathVariable BigInteger bankId, @Valid @RequestBody BankDTO bankDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,bankService.updateBank(bankId,bankDTO));
    }

    @ApiOperation("delete bank ")
    @DeleteMapping(COUNTRY_URL+DELETE_BANK)
    public ResponseEntity<Map<String,Object>> deleteBank(@PathVariable BigInteger bankId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,bankService.deleteBank(bankId));
    }

    @ApiOperation("get bank by id")
    @GetMapping(COUNTRY_URL+GET_BANK_BY_ID)
    public ResponseEntity<Map<String,Object>> getBankById(@PathVariable BigInteger bankId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,bankService.getBankById(bankId));
    }

    @ApiOperation("get All Bank ")
    @GetMapping(COUNTRY_URL+BANK)
    public ResponseEntity<Map<String,Object>> getAllBank(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,bankService.getAllBank(countryId));
    }
}
