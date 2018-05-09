package com.kairos.service.clause;


import com.kairos.ExceptionHandler.DuplicateDataException;
import com.kairos.ExceptionHandler.NotExists;
import com.kairos.persistance.model.clause.AccountType;
import com.kairos.persistance.repository.clause.AccountTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountTypeService extends MongoBaseService {


    @Inject
    private AccountTypeMongoRepository accountTypeRepository;


    public AccountType createAccountType(AccountType accountType) {

        AccountType exists = accountTypeRepository.findByTypeOfAccount(accountType.getTypeOfAccount());
        if (Optional.ofNullable(exists).isPresent()) {
            throw new DuplicateDataException("Account  Already Exists for name" + accountType.getTypeOfAccount());
        } else {
            AccountType accountType1 = new AccountType();
            accountType1.setTypeOfAccount(accountType.getTypeOfAccount());
            return save(accountType1);
        }
    }


    public AccountType getAccount(String typeOfAccount) {
        AccountType account = accountTypeRepository.findByTypeOfAccount(typeOfAccount);
        if (Optional.ofNullable(account).isPresent()) {
            return account;
        } else
            throw new NotExists("Account for account type ->" + typeOfAccount + " Not exists");
    }


    public List<AccountType> getAccountList(List<BigInteger> accountList) {
        List<AccountType> accountTypeList = new ArrayList<>();
        AccountType accountType;
        for (BigInteger accoundTypeId : accountList) {
            accountType = accountTypeRepository.findByid(accoundTypeId);
            if (accountType != null) {
                accountTypeList.add(accountType);
            } else {
                throw new NotExists("Account for id ->" + accoundTypeId + "not Exists");
            }
        }
        return accountTypeList;

    }



    public List<AccountType> getAllAccounts()
    {
        List<AccountType> accountTypeList=accountTypeRepository.findAll();
        if (accountTypeList!=null)
        {
            return accountTypeList;
        }
        else
            throw new NotExists("accounts not exist create account");
    }

}
