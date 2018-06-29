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

import static com.kairos.constants.ApiConstant.API_ACCOUNT_TYPE_URL;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
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
    public ResponseEntity<Object> createAccountType(@PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody AccountType accountType) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "basic_details id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.createAccountType(countryId, organizationId, accountType));
    }

    @ApiOperation(value = "account type by name")
    @GetMapping("/{name}")
    public ResponseEntity<Object> getAccountTypeByName(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable String name) {
        if (StringUtils.isBlank(name)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "typeOfAccount parameter is null or empty");
        } else if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "basic_details id cannot be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.getAccountByName(countryId, organizationId, name));

    }

    @ApiOperation(value = "all account type ")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllAccountTypes(@PathVariable Long countryId, @PathVariable Long organizationId) {
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "basic_details id can't be null");
        } else if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.getAllAccountType(countryId, organizationId));

    }

    @ApiOperation(value = "account type by id")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccountTypeById(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "basic_details id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.getAccountTypeById(countryId, organizationId, id));

    }


    @ApiOperation(value = "update account type name")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateAccountTypeName(@PathVariable BigInteger id, @PathVariable Long countryId, @PathVariable Long organizationId, @Valid @RequestBody AccountType accountType) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "basic_details id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.updateAccountTypeName(countryId, organizationId, id, accountType));
    }


    @ApiOperation(value = "delete account type by id ")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteAccountTypeById(@PathVariable Long countryId, @PathVariable Long organizationId, @PathVariable BigInteger id) {
        if (id == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null");
        }
        if (countryId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "basic_details id can't be null");
        }
        if (organizationId == null) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organization id can't be null");

        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.deleteAccountTypeById(countryId, organizationId, id));

    }


}
