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
import java.math.BigInteger;
import java.util.Set;
/*
*
*  created by bobby 20/4/2018
* */


@RestController
@Api(API_ACCOUNT_TYPE_URL)
@RequestMapping(API_ACCOUNT_TYPE_URL)
@CrossOrigin
public class AccountTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountTypeController.class);

    @Inject
    private AccountTypeService accountTypeService;


    @ApiOperation(value ="create new account type" )
    @PostMapping("/add_account")
    public ResponseEntity<Object> createAccountType( @RequestBody AccountType accountType) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.createAccountType(accountType));

    }

    @ApiOperation(value ="account type by name" )
    @GetMapping("/")
    public ResponseEntity<Object> getAccount(@RequestParam String typeOfAccount) {
        if (StringUtils.isBlank(typeOfAccount)) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "typeOfAccount parameter is null or empty");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.getAccountByName(typeOfAccount));

    }

 /*   @ApiOperation(value ="accounts  by ids list" )
    @RequestMapping(value = "/account_list", method = RequestMethod.POST)
    public ResponseEntity<Object> getAccountList(@RequestBody Set<BigInteger> accountIds) {
        if (accountIds.size()==0) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "Account List in Null");
        }
        return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.getAccountListByIds(accountIds));

    }
*/


    @ApiOperation(value ="all account type " )
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllAccounts() {
          return ResponseHandler.generateResponse(HttpStatus.OK, true, accountTypeService.getAllAccountType());

    }


}
