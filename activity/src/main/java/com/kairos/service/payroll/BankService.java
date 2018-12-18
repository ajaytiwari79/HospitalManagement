package com.kairos.service.payroll;
/*
 *Created By Pavan on 17/12/18
 *
 */

import com.kairos.dto.activity.payroll.BankDTO;
import com.kairos.persistence.model.payroll.Bank;
import com.kairos.persistence.repository.payroll.BankRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

@Service
public class BankService extends MongoBaseService {

    @Inject
    private BankRepository bankRepository;
    @Inject
    private ExceptionService exceptionService;

    public BankDTO createBank(Long countryId,BankDTO bankDTO){
        if(bankRepository.existsByNameIgnoreCaseAndDeletedFalse(bankDTO.getName())){
            exceptionService.duplicateDataException("data.already.exists",bankDTO.getName());
        }
        Bank bank=new Bank(null,bankDTO.getName(),bankDTO.getDescription(),bankDTO.getRegistrationNumber(),bankDTO.getInternationalAccountNumber(),bankDTO.getSwiftCode(),countryId);
        save(bank);
        bankDTO.setId(bank.getId());
        return bankDTO;
    }

    public BankDTO updateBank(BigInteger bankId, BankDTO bankDTO){
        if(bankRepository.existsByNameIgnoreCaseAndDeletedFalseAndIdNot(bankDTO.getName(),bankId)){
            exceptionService.duplicateDataException("data.already.exists",bankDTO.getName());
        }
        Bank bank=bankRepository.getByIdAndDeletedFalse(bankId);
        if(bank==null){
            exceptionService.dataNotFoundByIdException("data.not.found");
        }
        bank=new Bank(bank.getId(),bankDTO.getName(),bankDTO.getDescription(),bankDTO.getRegistrationNumber(),bankDTO.getInternationalAccountNumber(),bankDTO.getSwiftCode(),bank.getCountryId());
        save(bank);
        return bankDTO;
    }

    public boolean deleteBank(BigInteger bankId){
        bankRepository.safeDeleteById(bankId);
        return true;
    }

    public BankDTO getBankById(BigInteger bankId){
        return bankRepository.findByIdAndDeletedFalse(bankId);
    }

    public List<BankDTO> getAllBank(Long countryId){
        return bankRepository.findAllByCountryIdAndDeletedFalse(countryId);
    }
}
