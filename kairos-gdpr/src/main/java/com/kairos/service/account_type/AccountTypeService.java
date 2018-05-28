package com.kairos.service.account_type;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.persistance.repository.account_type.AccountTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AccountTypeService extends MongoBaseService {


    @Inject
    private AccountTypeMongoRepository accountTypeRepository;


    public AccountType createAccountType(Long countryId,AccountType accountType) {

        AccountType exists = accountTypeRepository.findByTypeOfAccount(countryId,accountType.getTypeOfAccount());
        if (Optional.ofNullable(exists).isPresent()) {
            throw new DuplicateDataException("Account  Already Exists for name" + accountType.getTypeOfAccount());
        } else {
            AccountType newAccount = new AccountType();
            newAccount.setTypeOfAccount(accountType.getTypeOfAccount());
            newAccount.setCountryId(countryId);
            return save(newAccount);
        }
    }


    public AccountType getAccountByName(Long countryId,String typeOfAccount) {
        AccountType account = accountTypeRepository.findByTypeOfAccount(countryId,typeOfAccount);
        if (Optional.ofNullable(account).isPresent()) {
            return account;
        } else
            throw new DataNotExists("Account for account type ->" + typeOfAccount + " Not exists");
    }


    public List<AccountType> getAccountTypeList(Set<BigInteger> ids) {
        return accountTypeRepository.getAccountTypeList(ids);

    }


    public List<AccountType> getAllAccountType()
    {
   return accountTypeRepository.getAllAccountType(UserContext.getCountryId());

    }




    public AccountType getAccountTypeById(Long countryId,BigInteger id) {

        AccountType exists = accountTypeRepository.findByIdAndNonDeleted(countryId,id);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw new DataNotFoundByIdException("Account  type not Exists for id" + id);
        } else {

            return exists;
        }
    }






}
