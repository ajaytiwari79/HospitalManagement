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
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.ActivityMessagesConstants.*;

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

    public PensionProviderDTO updatePensionProvider(BigInteger pensionProviderId,PensionProviderDTO pensionProviderDTO){
        PensionProvider alreadyExist = pensionProviderRepository.findByNameOrPaymentNumberAndIdNot(pensionProviderDTO.getName(),pensionProviderDTO.getPaymentNumber(),pensionProviderId);
        validatePensionProviderDetails(alreadyExist,pensionProviderDTO);
        PensionProvider pensionProvider=pensionProviderRepository.getByIdAndDeletedFalse(pensionProviderId);
        if(!Optional.ofNullable(pensionProvider).isPresent()){
            exceptionService.dataNotFoundByIdException(PENSION_PROVIDER_NOT_FOUND);
        }
        pensionProvider=new PensionProvider(pensionProvider.getId(),pensionProviderDTO.getName(),pensionProviderDTO.getPaymentNumber(),pensionProvider.getCountryId());
        pensionProviderRepository.save(pensionProvider);
        return pensionProviderDTO;

    }

    public boolean deletePensionProvider(BigInteger pensionProviderId){
        pensionProviderRepository.safeDeleteById(pensionProviderId);
        return true;
    }

    public PensionProviderDTO getPensionProviderById(BigInteger pensionProviderId){
        return pensionProviderRepository.findByIdAndDeletedFalse(pensionProviderId);
    }

    public List<PensionProviderDTO> getAllPensionProvider(Long countryId){
        return pensionProviderRepository.findAllByCountryIdAndDeletedFalseOrderByCreatedAtDesc(countryId);
    }

    private void validatePensionProviderDetails(PensionProvider pensionProvider, PensionProviderDTO pensionProviderDTO){
        if(pensionProvider!=null){
            if (pensionProviderDTO.getName().equalsIgnoreCase(pensionProvider.getName())) {
                exceptionService.duplicateDataException(PENSION_PROVIDER_ALREADY_EXISTS_NAME, pensionProviderDTO.getName());
            }
            if (pensionProviderDTO.getPaymentNumber().equalsIgnoreCase(pensionProvider.getPaymentNumber())) {
                exceptionService.duplicateDataException(PENSION_PROVIDER_ALREADY_EXISTS_PAYMENT_NUMBER, pensionProviderDTO.getPaymentNumber());
            }
        }

    }
}
