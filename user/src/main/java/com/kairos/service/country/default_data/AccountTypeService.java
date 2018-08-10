package com.kairos.service.country.default_data;


import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.UnitType;
import com.kairos.persistence.model.country.default_data.account_type.AccountType;
import com.kairos.persistence.model.country.default_data.account_type.AccountTypeQueryResult;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.default_data.AccountTypeGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.user.country.system_setting.AccountTypeDTO;
import com.kairos.util.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class AccountTypeService extends UserBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountTypeService.class);

    @Inject
    private AccountTypeGraphRepository accountTypeRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    public AccountTypeDTO createAccountType(Long countryId, AccountTypeDTO accountTypeDTO) {
        Optional<Country> country = countryGraphRepository.findById(countryId, 0);
        if (!country.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound");
        }

        Boolean exists = accountTypeRepository.checkAccountTypeExistInCountry(countryId, "(?i)" + accountTypeDTO.getName(), -1L);
        if (exists) {
            exceptionService.duplicateDataException("message.duplicate", "message.accountType", accountTypeDTO.getName());
        }
        AccountType newAccount = new AccountType(accountTypeDTO.getName(), country.get());
        accountTypeRepository.save(newAccount);
        accountTypeDTO.setId(newAccount.getId());
        return accountTypeDTO;
    }


    public AccountType getAccountByName(Long countryId, String name) {
        AccountType account = accountTypeRepository.findByName(countryId, name);
        return account;

    }

//
//    public List<AccountType> getAccountTypeList(Long countryId, Set<Long> ids) {
//        List<AccountType> accountTypes = accountTypeRepository.getAccountTypeList(ids);
//        if (accountTypes.size() != ids.size()) {
//            Set<BigInteger> accountTypeIds = new HashSet<>();
//            accountTypes.forEach(accountType -> {
//                accountTypeIds.add(accountType.getId());
//            });
//            ids.removeAll(accountTypeIds);
//            exceptionService.dataNotFoundByIdException("message.dataNotFound", "account type ", ids.iterator().next());
//        }
//        return accountTypes;
//    }


    public List<AccountTypeDTO> getAllAccountTypeByCountryId(Long countryId) {
        List<AccountType> accountTypes = accountTypeRepository.getAllAccountTypeByCountryId(countryId);
        // converted due to USE in both Microservice.
        List<AccountTypeDTO> accountTypeDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(accountTypes, AccountTypeDTO.class);
        return accountTypeDTOS;
    }


    public AccountType getAccountTypeById(Long countryId, Long id) {

        Optional<AccountType> accountType = accountTypeRepository.findById(id);
        if (!accountType.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.accountType", id);
        }
        return accountType.get();

    }

    public AccountTypeDTO updateAccountTypeName(Long countryId, Long id, AccountTypeDTO accountTypeDTO) {
        Optional<AccountType> accountType = accountTypeRepository.findById(accountTypeDTO.getId(), 0);
        if (!accountType.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitType.notFound", accountTypeDTO.getId());
        }

        Boolean exists = accountTypeRepository.checkAccountTypeExistInCountry(countryId, "(?i)" + accountTypeDTO.getName(), id);
        if (exists) {
            exceptionService.duplicateDataException("message.duplicate", "message.accountType", accountTypeDTO.getName());
        }
        accountType.get().setName(accountTypeDTO.getName());
        save(accountType.get());
        return accountTypeDTO;
    }


    public Boolean deleteAccountTypeById(Long countryId, Long id) {
        Optional<AccountType> accountType = accountTypeRepository.findById(id, 0);
        if (!accountType.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitType.notFound", id);
        }
        accountType.get().setDeleted(true);
        save(accountType.get());
        return true;
    }


}
