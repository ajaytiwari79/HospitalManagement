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

    public BankDTO updateBank(BigInteger bankId,Long countryId, BankDTO bankDTO){
        if(bankRepository.existsByNameIgnoreCaseAndDeletedFalseAndIdNot(bankDTO.getName(),bankId)){
            exceptionService.duplicateDataException("data.already.exists",bankDTO.getName());
        }
        Bank bank=bankRepository.findById(bankId);
        if(payRoll==null){
            exceptionService.dataNotFoundByIdException("data.not.found");
        }
        payRoll=new PayRoll(payRoll.getId(),payRollDTO.getName().trim(),payRollDTO.getCode(),payRollDTO.isActive());
        save(payRoll);
        return payRollDTO;
    }
//
//    public boolean deletePayRoll(BigInteger payRollId){
//        payRollRepository.safeDeleteById(payRollId);
//        return true;
//    }
//
//    public PayRollDTO getPayRollById(BigInteger payRollId){
//        return payRollRepository.findByIdAndDeletedFalse(payRollId);
//    }
//
//    public List<PayRollDTO> getAllPayRoll(){
//        return payRollRepository.findAllByDeletedFalse();
//    }
}
