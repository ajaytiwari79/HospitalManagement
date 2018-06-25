package com.kairos.service.account_type;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.persistance.repository.account_type.AccountTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AccountTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountTypeService.class);

    @Inject
    private AccountTypeMongoRepository accountTypeRepository;

    @Inject
    private ExceptionService exceptionService;


    public AccountType createAccountType(Long countryId,AccountType accountType) {

        AccountType exists = accountTypeRepository.findByTypeOfAccount(countryId,accountType.getName());
        if (Optional.ofNullable(exists).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate","message.accountType",accountType.getName());
        } else {
            AccountType newAccount = new AccountType();
            newAccount.setName(accountType.getName());
            newAccount.setCountryId(countryId);
            return save(newAccount);
        }
        return null;
    }


    public AccountType getAccountByName(Long countryId,String name) {
        AccountType account = accountTypeRepository.findByTypeOfAccount(countryId,name);
        if (Optional.ofNullable(account).isPresent()) {
            return account;
        } else
            throw new DataNotExists("Account for account type ->" + name + " Not exists");
    }


    public List<AccountType> getAccountTypeList(Long countryId,Set<BigInteger> ids) {
        return accountTypeRepository.getAccountTypeList(countryId,ids);

    }


    public List<AccountType> getAllAccountType(Long countryId)
    {
   return accountTypeRepository.getAllAccountType(countryId);
    }




    public AccountType getAccountTypeById(Long countryId,BigInteger id) {

        AccountType exists = accountTypeRepository.findByIdAndNonDeleted(countryId,id);
        if (!Optional.ofNullable(exists).isPresent()) {
          exceptionService.dataNotFoundByIdException("message.dataNotFound","message.accountType",id);
        }
        return exists;

    }

    public AccountType updateAccountName(Long countryId,BigInteger id,AccountType accountType) {

        AccountType exists = accountTypeRepository.findByNameAndNonDeleted(countryId,accountType.getName());
        if (Optional.ofNullable(exists).isPresent()) {
            throw  new DuplicateDataException("Account type exist for "+accountType.getName());
        }
        exists=accountTypeRepository.findByIdAndNonDeleted(countryId,id);
        exists.setName(accountType.getName());
        return exists;

    }


    public Boolean deleteAccountType(BigInteger id) {
        AccountType exists = accountTypeRepository.findByid(id);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw  new DataNotFoundByIdException("Account type exist for id "+id);
        }
        exists.setDeleted(true);
        save(exists);
        return true;

    }






}
