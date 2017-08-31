package com.kairos.controller;

import com.kairos.persistence.model.user.contract.Contract;
import com.kairos.service.contract.ContractService;
import com.kairos.service.country.CountryTimeTypeService;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test/")
public class ContractController {
    @Inject
    private ContractService contractService;
    @Inject
    private CountryTimeTypeService timeTypeService;

    // Contracts
    @RequestMapping(value = "/task_type/contracts/", method = RequestMethod.GET)
    @ApiOperation("Get All Contracts")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllAgreements() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,contractService.getAllContract());
    }

    @RequestMapping(value = "/task_type/contracts/", method = RequestMethod.POST)
    @ApiOperation("Get All Contracts")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> addAgreements(@RequestBody Contract contract) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,contractService.save(contract));
    }



    // TimeType
    @RequestMapping(value = "/task_type/timeType/", method = RequestMethod.GET)
    @ApiOperation("Get All TimeTypes")
    //@PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllTimeTypes() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,timeTypeService.getAllTimeType());
    }

    // Get All Roles
    @RequestMapping(value = "/task_type/roles/", method = RequestMethod.GET)
    @ApiOperation("Get All roles")
   // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true,timeTypeService.getAllTimeType());
    }


}
