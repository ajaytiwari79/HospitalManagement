package com.kairos.service.payroll;/*
 *Created By Pavan on 19/12/18
 *
 */

import com.kairos.dto.activity.payroll.PensionProviderDTO;
import com.kairos.persistence.model.payroll.PensionProvider;
import com.kairos.persistence.repository.payroll.PensionProviderRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class PensionProviderService extends MongoBaseService {

    @Inject
    private PensionProviderRepository pensionProviderRepository;

    @Inject
    private ExceptionService exceptionService;

    public PensionProviderDTO createPensionProvider(Long countryId, PensionProviderDTO pensionProviderDTO){
        PensionProvider pensionProvider = pensionProviderRepository.findByNameOrPaymentNumber(pensionProviderDTO.getName(),pensionProviderDTO.getPaymentNumber());
        validatePensionProviderDetails(pensionProvider,pensionProviderDTO);
        pensionProvider=new PensionProvider(null,pensionProviderDTO.getName(),pensionProviderDTO.getPaymentNumber(),countryId);
        pensionProviderRepository.save(pensionProvider);
        pensionProviderDTO.setId(pensionProvider.getId());
        return pensionProviderDTO;
    }

//    public BankDTO updateBank(BigInteger bankId, BankDTO bankDTO){
//        Bank alreadyExist = bankRepository.findByNameOrAccountNumberAndIdNot(bankId,bankDTO.getName(),bankDTO.getInternationalAccountNumber(),bankDTO.getRegistrationNumber(),bankDTO.getSwiftCode());
//        validateBankDetailsThrowsException(alreadyExist,bankDTO);
//        Bank bank=bankRepository.getByIdAndDeletedFalse(bankId);
//        if(bank==null){
//            exceptionService.dataNotFoundByIdException("bank.not.found");
//        }
//        bank=new Bank(bank.getId(),bankDTO.getName(),bankDTO.getDescription(),bankDTO.getRegistrationNumber(),bankDTO.getInternationalAccountNumber(),bankDTO.getSwiftCode(),bank.getCountryId());
//        save(bank);
//        return bankDTO;
//
//    }
//
//    public boolean deleteBank(BigInteger bankId){
//        bankRepository.safeDeleteById(bankId);
//        return true;
//    }
//
//    public BankDTO getBankById(BigInteger bankId){
//        return bankRepository.findByIdAndDeletedFalse(bankId);
//    }
//
//    public List<BankDTO> getAllBank(Long countryId){
//        return bankRepository.findAllByCountryIdAndDeletedFalseOrderByCreatedAtDesc(countryId);
//    }

    private void validatePensionProviderDetails(PensionProvider pensionProvider, PensionProviderDTO pensionProviderDTO){
        if(pensionProvider!=null){
            if (pensionProviderDTO.getName().equalsIgnoreCase(pensionProvider.getName())) {
                exceptionService.duplicateDataException("bank.already.exists.name", pensionProviderDTO.getName());
            }
            if (pensionProviderDTO.getPaymentNumber().equalsIgnoreCase(pensionProvider.getPaymentNumber())) {
                exceptionService.duplicateDataException("bank.already.exists.account", pensionProviderDTO.getPaymentNumber());
            }
        }

    }
}
