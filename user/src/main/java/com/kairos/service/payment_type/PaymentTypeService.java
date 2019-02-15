package com.kairos.service.payment_type;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.PaymentType;
import com.kairos.persistence.model.country.default_data.PaymentTypeDTO;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.payment_type.PaymentTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 9/1/17.
 */
@Transactional
@Service
public class PaymentTypeService{

    @Inject
    PaymentTypeGraphRepository paymentTypeGraphRepository;
    @Inject
    CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public PaymentTypeDTO createPaymentType(long countryId, PaymentTypeDTO paymentTypeDTO) {
     Country country = countryGraphRepository.findOne(countryId);
        PaymentType paymentType = null;
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        } else {
            Boolean paymentTypeExistInCountryByName = paymentTypeGraphRepository.paymentTypeExistInCountryByName(countryId, "(?i)" + paymentTypeDTO.getName(), -1L);
            if (paymentTypeExistInCountryByName) {
                exceptionService.duplicateDataException("error.PaymentType.name.exist");
            }
            paymentType = new PaymentType(paymentTypeDTO.getName(), paymentTypeDTO.getDescription());
            paymentType.setCountry(country);
            paymentTypeGraphRepository.save(paymentType);
        }
        paymentTypeDTO.setId(paymentType.getId());
        return paymentTypeDTO;

    }

    public List<PaymentTypeDTO> getPaymentTypes(long countryId) {
        return paymentTypeGraphRepository.findPaymentTypeByCountry(countryId);
    }

    public PaymentTypeDTO updatePaymentType(long countryId, PaymentTypeDTO paymentTypeDTO) {
        Boolean paymentTypeExistInCountryByName = paymentTypeGraphRepository.paymentTypeExistInCountryByName(countryId, "(?i)" + paymentTypeDTO.getName(), paymentTypeDTO.getId());
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
