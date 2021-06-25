package com.kairos.service.payroll;
/*
 *Created By Pavan on 17/12/18
 *
 */

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.payroll.*;
import com.kairos.persistence.model.payroll.*;
import com.kairos.persistence.repository.payroll.*;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectMapperUtils.copyPropertiesByMapper;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.ActivityMessagesConstants.*;

@Service
public class BankService{

    @Inject
    private BankRepository bankRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private PensionProviderRepository pensionProviderRepository;
    @Inject
    private StaffPensionProviderRepository staffPensionProviderRepository;
    @Inject
    private StaffBankDetailsRepository staffBankDetailsRepository;
    @Inject
    private OrganizationBankDetailsRepository organizationBankDetailsRepository;


    public BankDTO createBank(Long countryId, BankDTO bankDTO) {
        Bank bank = bankRepository.findByNameOrAccountNumber(bankDTO.getName(), bankDTO.getInternationalAccountNumber(), bankDTO.getRegistrationNumber(), bankDTO.getSwiftCode());
        validateBankDetails(bank, bankDTO);
        bank = new Bank(null, bankDTO.getName(), bankDTO.getDescription(), bankDTO.getRegistrationNumber(), bankDTO.getInternationalAccountNumber(), bankDTO.getSwiftCode(), countryId);
        bankRepository.save(bank);
        bankDTO.setId(bank.getId());
        return bankDTO;
    }

    public BankDTO updateBank(BigInteger bankId, BankDTO bankDTO) {
        Bank alreadyExist = bankRepository.findByNameOrAccountNumberAndIdNot(bankId, bankDTO.getName(), bankDTO.getInternationalAccountNumber(), bankDTO.getRegistrationNumber(), bankDTO.getSwiftCode());
        validateBankDetails(alreadyExist, bankDTO);
        Bank bank = bankRepository.getByIdAndDeletedFalse(bankId);
        if (!Optional.ofNullable(bank).isPresent()) {
            exceptionService.dataNotFoundByIdException(BANK_NOT_FOUND);
        }
        bank = new Bank(bank.getId(), bankDTO.getName(), bankDTO.getDescription(), bankDTO.getRegistrationNumber(), bankDTO.getInternationalAccountNumber(), bankDTO.getSwiftCode(), bank.getCountryId());
        bankRepository.save(bank);
        return bankDTO;

    }

    public boolean deleteBank(BigInteger bankId) {
        bankRepository.safeDeleteById(bankId);
        return true;
    }

    public BankDTO getBankById(BigInteger bankId) {
        return bankRepository.findByIdAndDeletedFalse(bankId);
    }

    public List<BankDTO> getAllBank(Long countryId) {
        List<BankDTO> bankDTOS = bankRepository.findAllByCountryIdAndDeletedFalseOrderByCreatedAtDesc(countryId);
        bankDTOS.forEach(bankDTO -> {
            bankDTO.setCountryId(countryId);
        });
        return bankDTOS;
    }

    private void validateBankDetails(Bank bank, BankDTO bankDTO) {
        if (Optional.ofNullable(bank).isPresent()) {
            if (bankDTO.getName().equalsIgnoreCase(bank.getName())) {
                exceptionService.duplicateDataException(BANK_ALREADY_EXISTS_NAME, bankDTO.getName());
            }
            if (bankDTO.getInternationalAccountNumber().equalsIgnoreCase(bank.getInternationalAccountNumber())) {
                exceptionService.duplicateDataException(BANK_ALREADY_EXISTS_ACCOUNT, bankDTO.getInternationalAccountNumber());
            }
            if (bankDTO.getRegistrationNumber().equalsIgnoreCase(bank.getRegistrationNumber())) {
                exceptionService.duplicateDataException(BANK_ALREADY_EXISTS_REG, bankDTO.getRegistrationNumber());
            }
            if (bankDTO.getSwiftCode().equalsIgnoreCase(bank.getSwiftCode())) {
                exceptionService.duplicateDataException(BANK_ALREADY_EXISTS_SWIFT, bankDTO.getSwiftCode());
            }
        }

    }

    public StaffBankAndPensionProviderDetailsDTO getBankDetailsOfStaff(Long staffId, Long organizationId) {
        Long countryId = userIntegrationService.getCountryIdOfOrganization(organizationId);
        List<BankDTO> bankDTOS = bankRepository.findAllByCountryIdAndDeletedFalseOrderByCreatedAtDesc(countryId);
        StaffBankDetails staffOfficialBank = staffBankDetailsRepository.findByStaffIdAndDeletedFalse(staffId);
        List<PensionProviderDTO> pensionProviderDTOS = pensionProviderRepository.findAllByCountryIdAndDeletedFalseOrderByCreatedAtDesc(countryId);
        StaffPensionProviderDetails staffPensionProvider = staffPensionProviderRepository.findByStaffIdAndDeletedFalse(staffId);
        return new StaffBankAndPensionProviderDetailsDTO(bankDTOS, copyPropertiesByMapper(staffOfficialBank, StaffBankDetailsDTO.class), pensionProviderDTOS, copyPropertiesByMapper(staffPensionProvider, StaffPensionProviderDetailsDTO.class));
    }

    public boolean linkBankDetailsForStaff(Long staffId, StaffBankAndPensionProviderDetailsDTO staffBankDetailsDTO){
        if (isNotNull(staffBankDetailsDTO.getStaffOfficialBank())) {
            if(!staffBankDetailsDTO.getStaffOfficialBank().isUseNemkontoAccount()){
                Bank bank = bankRepository.getByIdAndDeletedFalse(staffBankDetailsDTO.getStaffOfficialBank().getBankId());
                if(isNull(bank)){
                    exceptionService.dataNotFoundException(BANK_NOT_FOUND);
                }
            }
            StaffBankDetails staffBank = staffBankDetailsRepository.findByStaffIdAndDeletedFalse(staffId);
            if (isNull(staffBank)) {
                staffBank = new StaffBankDetails();
            }
            staffBank.setAccountNumber(staffBankDetailsDTO.getStaffOfficialBank().getAccountNumber());
            staffBank.setStaffId(staffId);
            staffBank.setUseNemkontoAccount(staffBankDetailsDTO.getStaffOfficialBank().isUseNemkontoAccount());
            staffBank.setBankId(staffBankDetailsDTO.getStaffOfficialBank().getBankId());
            staffBankDetailsRepository.save(staffBank);
        }
        if (isNotNull(staffBankDetailsDTO.getStaffPensionProvider())) {
            PensionProvider pensionProvider = pensionProviderRepository.getByIdAndDeletedFalse(staffBankDetailsDTO.getStaffPensionProvider().getPensionProviderId());
            if(isNull(pensionProvider)){
                exceptionService.dataNotFoundException(PENSION_PROVIDER_NOT_FOUND);
            }
            StaffPensionProviderDetails staffPensionProviderDetails = staffPensionProviderRepository.findByStaffIdAndDeletedFalse(staffId);
            if (isNull(staffPensionProviderDetails)) {
                staffPensionProviderDetails = new StaffPensionProviderDetails();
            }
            staffPensionProviderDetails.setPensionProviderId(staffBankDetailsDTO.getStaffPensionProvider().getPensionProviderId());
            staffPensionProviderDetails.setStaffId(staffId);
            staffPensionProviderRepository.save(staffPensionProviderDetails);
        }
        return true;
    }

    public OrganizationBankDetailsAndBanksDTO getBankDetailsOfOrganization(Long organizationId) {
        Long countryId = userIntegrationService.getCountryIdOfOrganization(organizationId);
        List<BankDTO> bankDTOS = bankRepository.findAllByCountryIdAndDeletedFalseOrderByCreatedAtDesc(countryId);
        OrganizationBankDetails organizationBankDetails = organizationBankDetailsRepository.findByOrganizationIdAndDeletedFalse(organizationId);
        return new OrganizationBankDetailsAndBanksDTO(bankDTOS, copyPropertiesByMapper(organizationBankDetails, OrganizationBankDetailsDTO.class));
    }

    public boolean linkBankDetailsForOrganization(Long organizationId, OrganizationBankDetailsDTO organizationBankDetailsDTO) {
        Bank bank = bankRepository.getByIdAndDeletedFalse(organizationBankDetailsDTO.getBankId());
        if(isNull(bank)){
            exceptionService.dataNotFoundException(BANK_NOT_FOUND);
        }
        OrganizationBankDetails organizationBankDetails = organizationBankDetailsRepository.findByOrganizationIdAndDeletedFalse(organizationId);
        if (isNull(organizationBankDetails)) {
            organizationBankDetails = new OrganizationBankDetails();
        }
        organizationBankDetails.setOrganizationId(organizationId);
        organizationBankDetails.setBankId(organizationBankDetailsDTO.getBankId());
        organizationBankDetails.setAccountNumber(organizationBankDetailsDTO.getAccountNumber());
        organizationBankDetails.setEmail(organizationBankDetailsDTO.getEmail());
        organizationBankDetailsRepository.save(organizationBankDetails);
        return true;
    }

    public Map<String, TranslationInfo> updateTranslation(BigInteger paymentTypeId, Map<String,TranslationInfo> translations) {
        Bank bank =bankRepository.findOne(paymentTypeId);
        bank.setTranslations(translations);
        bankRepository.save(bank);
        return bank.getTranslations();
    }

}
