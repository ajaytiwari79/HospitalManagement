package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.Currency;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CurrencyGraphRepository;
import com.kairos.util.FormatUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 9/1/17.
 */
@Service
@Transactional
public class CurrencyService {

    @Inject
    CurrencyGraphRepository currencyGraphRepository;

    @Inject
    CountryGraphRepository countryGraphRepository;

    public HashMap<String, Object> saveCurrency(long countryId, Currency currency) {

        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            return null;
        }
        currency.setCountry(country);
        currencyGraphRepository.save(currency);
        return prepareCurrencyResponse(currency);

    }

    public List<Map<String,Object>> getCurrencies(long countryId) {
        if (countryId!=0){
            return FormatUtil.formatNeoResponse(currencyGraphRepository.getCurrencies(countryId));
        }
        return null;
    }

    public HashMap<String, Object> updateCurrency(Currency currency) {
        Currency objectToUpdate = currencyGraphRepository.findOne(currency.getId());
        if (objectToUpdate == null) {
            return null;
        }
        objectToUpdate.setName(currency.getName());
        objectToUpdate.setDescription(currency.getDescription());
        objectToUpdate.setCurrencyCode(currency.getCurrencyCode());

        currencyGraphRepository.save(objectToUpdate);
        return prepareCurrencyResponse(currency);
    }

    public boolean deleteCurrency(long currencyId) {
        Currency currency = currencyGraphRepository.findOne(currencyId);
        if (currency == null) {
            return false;
        }
        currency.setDeleted(true);
        currencyGraphRepository.save(currency);
        return true;
    }

    private HashMap<String, Object> prepareCurrencyResponse(Currency currency) {
        HashMap<String, Object> response = new HashMap<>(2);
        response.put("id", currency.getId());
        response.put("name", currency.getName());
        response.put("description", currency.getDescription());
        response.put("currencyCode", currency.getCurrencyCode());
        return response;
    }

    public Currency getCurrencyByCountryId(Long countryId){
       return  currencyGraphRepository.findFirstByCountryIdAndDeletedFalse(countryId);
    }
}
