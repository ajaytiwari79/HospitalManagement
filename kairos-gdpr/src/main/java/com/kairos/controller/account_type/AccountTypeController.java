package com.kairos.controller.account_type;


import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.service.account_type.AccountTypeService;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kairos.constant.ApiConstant.API_ACCOUNT_TYPE_URL;
import static com.kairos.constant.ApiConstant.PARENT_ORGABNIZATION;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.Set;
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
    public ResponseEntity<Object> createAccountType(@PathVariable Long countryId, @Valid @RequestBody AccountType accountType) {
        if (countryId != null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.createAccountType(countryId, accountType));
        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_GATEWAY, false, "country id can not be null");
    }

    @ApiOperation(value = "account type by name")
    @GetMapping("/{name}")
    public ResponseEntity<Object> getAccount(@PathVariable Long countryId, @PathVariable String name) {
        if (StringUtils.isBlank(name)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "typeOfAccount parameter is null or empty");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id cannot be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.getAccountByName(countryId, name));

    }

    @ApiOperation(value = "all account type ")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllAccounts(@PathVariable Long countryId) {
        if (countryId==null)
        {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.getAllAccountType(countryId));

    }

    @ApiOperation(value = "account type by name")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccountType(@PathVariable Long countryId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "country id cannot be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.getAccountTypeById(countryId, id));

    }


    @ApiOperation(value = "update account type ")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateAccountName(@PathVariable BigInteger id, @PathVariable Long countryId, @Valid @RequestBody AccountType accountType) {
        if (id == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.updateAccountName(countryId, id, accountType));
    }


    @ApiOperation(value = "update account type ")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteAccountType(@PathVariable Long countryId,@PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.generateResponse(HttpStatus.BAD_GATEWAY, false, "id cannot be null");
        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.deleteAccountType(id));

    }


}
