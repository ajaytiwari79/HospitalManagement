package com.kairos.service.payment_type;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.PaymentType;
import com.kairos.persistence.model.country.default_data.PaymentTypeDTO;
import com.kairos.persistence.model.country.reason_code.ReasonCodeResponseDTO;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.payment_type.PaymentTypeGraphRepository;
import com.kairos.service.country.CountryService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 9/1/17.
 */
@Transactional
@Service
public class PaymentTypeService {

    @Inject
    private PaymentTypeGraphRepository paymentTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CountryService countryService;

    public PaymentTypeDTO createPaymentType(long countryId, PaymentTypeDTO paymentTypeDTO) {
        Country country = countryService.findById(countryId);
            boolean paymentTypeExistInCountryByName = paymentTypeGraphRepository.paymentTypeExistInCountryByName(countryId, "(?i)" + paymentTypeDTO.getName(), -1L);
            if (paymentTypeExistInCountryByName) {
                exceptionService.duplicateDataException("error.PaymentType.name.exist");
            }
            PaymentType paymentType  = new PaymentType(paymentTypeDTO.getName(), paymentTypeDTO.getDescription());
            paymentType.setCountry(country);
            paymentTypeGraphRepository.save(paymentType);
        paymentTypeDTO.setId(paymentType.getId());
        return paymentTypeDTO;

    }

    public List<PaymentTypeDTO> getPaymentTypes(long countryId) {
        List<PaymentType> paymentTypes = paymentTypeGraphRepository.findPaymentTypeByCountry(countryId);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(paymentTypes, PaymentTypeDTO.class);
    }

    public PaymentTypeDTO updatePaymentType(long countryId, PaymentTypeDTO paymentTypeDTO) {
        boolean paymentTypeExistInCountryByName = paymentTypeGraphRepository.paymentTypeExistInCountryByName(countryId, "(?i)" + paymentTypeDTO.getName(), paymentTypeDTO.getId());
        if (paymentTypeExistInCountryByName) {
            exceptionService.duplicateDataException("error.PaymentType.name.exist");
        }
        PaymentType currentPaymentType = paymentTypeGraphRepository.findOne(paymentTypeDTO.getId());
        if (currentPaymentType != null) {
            currentPaymentType.setName(paymentTypeDTO.getName());
            currentPaymentType.setDescription(paymentTypeDTO.getDescription());
            paymentTypeGraphRepository.save(currentPaymentType);
        }
        return paymentTypeDTO;
    }

    public boolean deletePaymentType(long paymentTypeId) {
        PaymentType paymentType = paymentTypeGraphRepository.findOne(paymentTypeId);
        if (paymentType != null) {
            paymentType.setEnabled(false);
            paymentTypeGraphRepository.save(paymentType);
        } else {
            exceptionService.dataNotFoundByIdException("error.PaymentType.notfound");
        }
        return true;
    }
}
