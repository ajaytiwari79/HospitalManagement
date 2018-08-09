package com.kairos.service.account_type;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.persistance.repository.account_type.AccountTypeMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class AccountTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountTypeService.class);

    @Inject
    private AccountTypeMongoRepository accountTypeRepository;

    @Inject
    private ExceptionService exceptionService;


    public AccountType createAccountType(Long countryId, AccountType accountType) {

        AccountType exists = accountTypeRepository.findByName(countryId, accountType.getName());
        if (Optional.ofNullable(exists).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.accountType", accountType.getName());
        }
        AccountType newAccount = new AccountType();
        newAccount.setName(accountType.getName());
        newAccount.setCountryId(countryId);
        return accountTypeRepository.save(newAccount);

    }


    public AccountType getAccountByName(Long countryId, String name) {
        AccountType account = accountTypeRepository.findByName(countryId, name);
        if (Optional.ofNullable(account).isPresent()) {
            return account;
        } else
            throw new DataNotExists("Account for account type ->" + name + " Not exists");
    }


    public List<AccountType> getAccountTypeList(Long countryId, Set<BigInteger> ids) {
        List<AccountType> accountTypes = accountTypeRepository.getAccountTypeList(countryId, ids);
        if (accountTypes.size() != ids.size()) {
            Set<BigInteger> accountTypeIds = new HashSet<>();
            accountTypes.forEach(accountType -> {
                accountTypeIds.add(accountType.getId());
            });
            ids.removeAll(accountTypeIds);
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "account type ", ids.iterator().next());
        }
        return accountTypes;
    }


    public List<AccountType> getAllAccountType(Long countryId) {
        return accountTypeRepository.getAllAccountType(countryId);
    }


    public AccountType getAccountTypeById(Long countryId, BigInteger id) {

        AccountType exists = accountTypeRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exists).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.accountType", id);
        }
        return exists;

    }

    public AccountType updateAccountTypeName(Long countryId, BigInteger id, AccountType accountType) {

        AccountType previousAccountType = accountTypeRepository.findByName(countryId, accountType.getName());
        if (Optional.ofNullable(previousAccountType).isPresent() && !id.equals(previousAccountType.getId())) {
            throw new DuplicateDataException("Account type exist for " + accountType.getName());
        }
        previousAccountType = accountTypeRepository.findByIdAndNonDeleted(countryId, id);
        previousAccountType.setName(accountType.getName());
        return accountTypeRepository.save(previousAccountType);

    }


    public Boolean deleteAccountTypeById(Long countryId, BigInteger id) {
        AccountType exists = accountTypeRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw new DataNotFoundByIdException("Account type exist for id " + id);
        }
        delete(exists);
        return true;

    }


}
