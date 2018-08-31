package com.kairos.controller.country.default_data;


import com.kairos.service.country.default_data.AccountTypeService;
import com.kairos.user.country.system_setting.AccountTypeDTO;
import com.kairos.util.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

import static com.kairos.constants.ApiConstants.API_ACCOUNT_TYPE_URL;
/*
 *
 *  created by bobby 20/4/2018
 * */


@RestController
@Api(API_ACCOUNT_TYPE_URL)
@RequestMapping(API_ACCOUNT_TYPE_URL)
public class AccountTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountTypeController.class);

    @Inject
    private AccountTypeService accountTypeService;


    @ApiOperation(value = "create new account type")
    @PostMapping("/add")
    public ResponseEntity<Map<String,Object>> createAccountType(@PathVariable Long countryId, @Valid @RequestBody AccountTypeDTO accountTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.createAccountType(countryId, accountTypeDTO));
    }


    @ApiOperation(value = "all account type ")
    @GetMapping(value = "/all" )
    public ResponseEntity<Map<String,Object>> getAllAccountTypes(@PathVariable Long countryId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.getAllAccountTypeByCountryId(countryId));

    }

    @ApiOperation(value = "account type by id")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String,Object>> getAccountTypeById(@PathVariable Long countryId, @PathVariable Long id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.getAccountTypeById(countryId, id));

    }


    @ApiOperation(value = "update account type name")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Map<String,Object>> updateAccountTypeName(@PathVariable Long id, @PathVariable Long countryId, @Valid @RequestBody AccountTypeDTO accountTypeDTO) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.updateAccountTypeName(countryId, id, accountTypeDTO));
    }


    @ApiOperation(value = "delete account type by id ")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Map<String,Object>> deleteAccountTypeById(@PathVariable Long countryId, @PathVariable Long id) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.deleteAccountTypeById(countryId, id));

    }


}
