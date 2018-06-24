package com.kairos.service.account_type;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.persistance.repository.account_type.AccountTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
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


    public AccountType createAccountType(Long countryId,Long organizationId,AccountType accountType) {

        AccountType exists = accountTypeRepository.findByName(countryId,organizationId,accountType.getName());
        if (Optional.ofNullable(exists).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate","message.accountType",accountType.getName());
        }
            AccountType newAccount = new AccountType();
            newAccount.setName(accountType.getName());
            newAccount.setCountryId(countryId);
            newAccount.setOrganizationId(organizationId);
            return save(newAccount);
        }


    public AccountType getAccountByName(Long countryId,Long organizationId,String name) {
        AccountType account = accountTypeRepository.findByName(countryId,organizationId,name);
        if (Optional.ofNullable(account).isPresent()) {
            return account;
        } else
            throw new DataNotExists("Account for account type ->" + name + " Not exists");
    }


    public List<AccountType> getAccountTypeList(Long countryId,Long organizationId,Set<BigInteger> ids) {
        return accountTypeRepository.getAccountTypeList(countryId,organizationId,ids);

    }


    public List<AccountType> getAllAccountType(Long countryId,Long organizationId)
    {
   return accountTypeRepository.getAllAccountType(countryId,organizationId);
    }




    public AccountType getAccountTypeById(Long countryId,Long organizationId,BigInteger id) {

        AccountType exists = accountTypeRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exists).isPresent()) {
          exceptionService.dataNotFoundByIdException("message.dataNotFound","message.accountType",id);
        }
        return exists;

    }

    public AccountType updateAccountName(Long countryId,Long organizationId,BigInteger id,AccountType accountType) {

        AccountType exists = accountTypeRepository.findByName(countryId,organizationId,accountType.getName());
        if (Optional.ofNullable(exists).isPresent()&&!id.equals(exists.getId())) {
            throw  new DuplicateDataException("Account type exist for "+accountType.getName());
        }
        exists=accountTypeRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        exists.setName(accountType.getName());
        return exists;

    }


    public Boolean deleteAccountType(Long countryId,Long organizationId,BigInteger id) {
        AccountType exists = accountTypeRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw  new DataNotFoundByIdException("Account type exist for id "+id);
        }
        exists.setDeleted(true);
        save(exists);
        return true;

    }






}
