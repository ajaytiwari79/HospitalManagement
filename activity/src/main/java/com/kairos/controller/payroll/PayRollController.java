package com.kairos.controller.payroll;

import com.kairos.dto.activity.payroll.PayRollDTO;
import com.kairos.service.payroll.PayRollService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import static com.kairos.constants.ApiConstants.API_ORGANIZATION_URL;
import static com.kairos.constants.ApiConstants.COUNTRY_ID;
import static com.kairos.constants.ApiConstants.COUNTRY_URL;
import static com.kairos.constants.payroll.PayRollConstants.*;

/*
 *Created By Pavan on 14/12/18
 *
 */
@RestController
@RequestMapping(API_ORGANIZATION_URL)
public class PayRollController {
    @Inject
    private PayRollService payRollService;

    @ApiOperation("Create PayRoll at System level")
    @PostMapping(PAYROLL)
    public ResponseEntity<Map<String,Object>> createPayRoll(@Valid @RequestBody PayRollDTO payRollDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.createPayRoll(payRollDTO));
    }

    @ApiOperation("update PayRoll at System level")
    @PutMapping(UPDATE_PAYROLL)
    public ResponseEntity<Map<String,Object>> updatePayRoll(@PathVariable BigInteger payRollId, @Valid @RequestBody PayRollDTO payRollDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.updatePayRoll(payRollId,payRollDTO));
    }

    @ApiOperation("delete PayRoll at System level")
    @DeleteMapping(DELETE_PAYROLL)
    public ResponseEntity<Map<String,Object>> deletePayRoll(@PathVariable BigInteger payRollId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.deletePayRoll(payRollId));
    }

    @ApiOperation("get PayRoll at System level")
    @GetMapping(GET_PAYROLL_BY_ID)
    public ResponseEntity<Map<String,Object>> getPayRollById(@PathVariable BigInteger payRollId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.getPayRollById(payRollId));
    }

    @ApiOperation("get All PayRoll at System level")
    @GetMapping(PAYROLL)
    public ResponseEntity<Map<String,Object>> getAllPayRoll(){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.getAllPayRoll());
    }

    @ApiOperation("link PayRoll to country level from System level")
    @PutMapping(COUNTRY_URL+UPDATE_PAYROLL)
    public ResponseEntity<Map<String,Object>> linkPayRollWithCountry(@PathVariable BigInteger payRollId,@PathVariable Long countryId,@RequestParam("checked") boolean checked){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.linkPayRollWithCountry(countryId,payRollId,checked));
    }

    @ApiOperation("get PayRoll at country level")
    @GetMapping(COUNTRY_URL+PAYROLL)
    public ResponseEntity<Map<String,Object>> getAllPayRollOfCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.getAllPayRollOfCountry(countryId));
    }



}
