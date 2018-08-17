package com.kairos.service.payment_type;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.user.payment_type.PaymentType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.payment_type.PaymentTypeGraphRepository;
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

    public HashMap<String, Object> createPaymentType(long countryId, PaymentType paymentType) {

        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            return null;
        }
        paymentType.setCountry(country);
        paymentTypeGraphRepository.save(paymentType);
        return preparePaymentTypeResponse(paymentType);

    }

    public List<Map<String, Object>> getPaymentTypes(long countryId) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String,Object> paymentType : paymentTypeGraphRepository.getPaymentTypes(countryId)) {
            list.add((Map<String, Object>) paymentType.get("data"));
        }
        return list;
    }

    public HashMap<String, Object> updatePaymentType(PaymentType paymentType) {
        PaymentType objectToUpdate = paymentTypeGraphRepository.findOne(paymentType.getId());
        if (objectToUpdate == null) {
            return null;
        }
        objectToUpdate.setName(paymentType.getName());
        objectToUpdate.setDescription(paymentType.getDescription());
        paymentTypeGraphRepository.save(objectToUpdate);
        return preparePaymentTypeResponse(paymentType);
    }

    public boolean deletePaymentType(long paymentTypeId) {
        PaymentType paymentType = paymentTypeGraphRepository.findOne(paymentTypeId);
        if (paymentType == null) {
            return false;
        }
        paymentType.setEnabled(false);
        paymentTypeGraphRepository.save(paymentType);
        return true;
    }

    private HashMap<String, Object> preparePaymentTypeResponse(PaymentType paymentType) {
        HashMap<String, Object> response = new HashMap<>(2);
        response.put("id", paymentType.getId());
        response.put("name", paymentType.getName());
        response.put("description", paymentType.getDescription());

        return response;
    }
}
