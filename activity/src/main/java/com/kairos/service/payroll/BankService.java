package com.kairos.service.payroll;
/*
 *Created By Pavan on 17/12/18
 *
 */

import com.kairos.dto.activity.payroll.BankDTO;
import com.kairos.dto.activity.payroll.PensionProviderDTO;
import com.kairos.dto.activity.payroll.StaffBankDetailsDTO;
import com.kairos.persistence.model.payroll.Bank;
import com.kairos.persistence.repository.payroll.BankRepository;
import com.kairos.persistence.repository.payroll.PensionProviderRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;

@Service
public class BankService extends MongoBaseService {

    @Inject
    private BankRepository bankRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject private GenericIntegrationService genericIntegrationService;
    @Inject private PensionProviderRepository pensionProviderRepository;


    public BankDTO createBank(Long countryId,BankDTO bankDTO){
        Bank bank = bankRepository.findByNameOrAccountNumber(bankDTO.getName(),bankDTO.getInternationalAccountNumber(),bankDTO.getRegistrationNumber(),bankDTO.getSwiftCode());
        validateBankDetails(bank,bankDTO);
        bank=new Bank(null,bankDTO.getName(),bankDTO.getDescription(),bankDTO.getRegistrationNumber(),bankDTO.getInternationalAccountNumber(),bankDTO.getSwiftCode(),countryId);
        bankRepository.save(bank);
        bankDTO.setId(bank.getId());
        return bankDTO;
    }

    public BankDTO updateBank(BigInteger bankId, BankDTO bankDTO){
        Bank alreadyExist = bankRepository.findByNameOrAccountNumberAndIdNot(bankId,bankDTO.getName(),bankDTO.getInternationalAccountNumber(),bankDTO.getRegistrationNumber(),bankDTO.getSwiftCode());
        validateBankDetails(alreadyExist,bankDTO);
        Bank bank=bankRepository.getByIdAndDeletedFalse(bankId);
        if(!Optional.ofNullable(bank).isPresent()){
            exceptionService.dataNotFoundByIdException("bank.not.found");
        }
        bank=new Bank(bank.getId(),bankDTO.getName(),bankDTO.getDescription(),bankDTO.getRegistrationNumber(),bankDTO.getInternationalAccountNumber(),bankDTO.getSwiftCode(),bank.getCountryId());
        bankRepository.save(bank);
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
        return bankRepository.findAllByCountryIdAndDeletedFalseOrderByCreatedAtDesc(countryId);
    }

    private void validateBankDetails(Bank bank, BankDTO bankDTO){
        if(Optional.ofNullable(bank).isPresent()){
            if (bankDTO.getName().equalsIgnoreCase(bank.getName())) {
                exceptionService.duplicateDataException("bank.already.exists.name", bankDTO.getName());
            }
            if (bankDTO.getInternationalAccountNumber().equalsIgnoreCase(bank.getInternationalAccountNumber())) {
                exceptionService.duplicateDataException("bank.already.exists.account", bankDTO.getInternationalAccountNumber());
            }
            if (bankDTO.getRegistrationNumber().equalsIgnoreCase(bank.getRegistrationNumber())) {
                exceptionService.duplicateDataException("bank.already.exists.reg", bankDTO.getRegistrationNumber());
            }
            if (bankDTO.getSwiftCode().equalsIgnoreCase(bank.getSwiftCode())) {
                exceptionService.duplicateDataException("bank.already.exists.swift", bankDTO.getSwiftCode());
            }
        }

    }

    public StaffBankDetailsDTO getBankDetailsOfStaff(Long staffId,Long organizationId){
        Long countryId = genericIntegrationService.getCountryIdOfOrganization(organizationId);
        List<BankDTO> bankDTOS = bankRepository.findAllByCountryIdAndDeletedFalseOrderByCreatedAtDesc(countryId);
        BankDTO staffOfficialBank = bankRepository.findByStaffIdAndDeletedfalse(staffId);
        List<PensionProviderDTO> pensionProviderDTOS = pensionProviderRepository.findAllByCountryIdAndDeletedFalseOrderByCreatedAtDesc(countryId);
        PensionProviderDTO staffPensionProvider = pensionProviderRepository.findByStaffIdAndDeletedFalse(staffId);
        return new StaffBankDetailsDTO(bankDTOS,staffOfficialBank,pensionProviderDTOS,staffPensionProvider);
    }

    public boolean linkBankDetailsForStaff(StaffBankDetailsDTO staffBankDetailsDTO){
        if(isNotNull(staffBankDetailsDTO.getStaffOfficialBank())){

        }
        if (isNotNull(staffBankDetailsDTO.getStaffPensionProvider())){

        }
        return true;
    }

}
