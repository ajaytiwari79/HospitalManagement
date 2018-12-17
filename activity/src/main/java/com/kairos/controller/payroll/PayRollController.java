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
    @PostMapping(value = "/payroll")
    public ResponseEntity<Map<String,Object>> createPayRoll(@Valid @RequestBody PayRollDTO payRollDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.createPayRoll(payRollDTO));
    }

    @ApiOperation("update PayRoll at System level")
    @PutMapping(value = "/payroll/{payRollId}")
    public ResponseEntity<Map<String,Object>> updatePayRoll(@PathVariable BigInteger payRollId, @Valid @RequestBody PayRollDTO payRollDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.updatePayRoll(payRollId,payRollDTO));
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

    @ApiOperation("link PayRoll to country level from System level")
    @PutMapping(value = "/payroll")
    public ResponseEntity<Map<String,Object>> linkPayRollWithCountry(@RequestParam("countryId") Long countryId,@RequestParam("action") String action,@RequestBody Set<BigInteger> payRollIds){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.linkPayRollWithCountry(countryId,payRollIds,action));
    }

    @ApiOperation("get PayRoll at country level")
    @GetMapping(value = "/country/{countryId}/payroll")
    public ResponseEntity<Map<String,Object>> getAllPayRollOfCountry(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,payRollService.getAllPayRollOfCountry(countryId));
    }



}
