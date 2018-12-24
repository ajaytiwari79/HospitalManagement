package com.kairos.controller.payroll;
/*
 *Created By Pavan on 19/12/18
 *
 */

import com.kairos.dto.activity.payroll.PensionProviderDTO;
import com.kairos.service.payroll.BankService;
import com.kairos.service.payroll.PensionProviderService;
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
public class PensionProviderController {

    @Inject
    private PensionProviderService pensionProviderService;

    @ApiOperation("Create pension provider")
    @PostMapping(COUNTRY_URL+PENSION_PROVIDER)
    public ResponseEntity<Map<String,Object>> createPensionProvider(@PathVariable Long countryId, @Valid @RequestBody PensionProviderDTO pensionProviderDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,pensionProviderService.createPensionProvider(countryId,pensionProviderDTO));
    }

    @ApiOperation("update pension provider")
    @PutMapping(COUNTRY_URL+UPDATE_PENSION_PROVIDER)
    public ResponseEntity<Map<String,Object>> updatePensionProvider(@PathVariable BigInteger pensionProviderId, @Valid @RequestBody PensionProviderDTO pensionProviderDTO){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,pensionProviderService.updatePensionProvider(pensionProviderId,pensionProviderDTO));
    }

    @ApiOperation("delete pension provider ")
    @DeleteMapping(COUNTRY_URL+DELETE_PENSION_PROVIDER)
    public ResponseEntity<Map<String,Object>> deletePensionProvider(@PathVariable BigInteger pensionProviderId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,pensionProviderService.deletePensionProvider(pensionProviderId));
    }

    @ApiOperation("get pension provider by id")
    @GetMapping(COUNTRY_URL+GET_PENSION_PROVIDER_BY_ID)
    public ResponseEntity<Map<String,Object>> getPensionProviderById(@PathVariable BigInteger pensionProviderId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,pensionProviderService.getPensionProviderById(pensionProviderId));
    }

    @ApiOperation("get All pension provider ")
    @GetMapping(COUNTRY_URL+PENSION_PROVIDER)
    public ResponseEntity<Map<String,Object>> getAllPensionProvider(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,pensionProviderService.getAllPensionProvider(countryId));
    }
}
