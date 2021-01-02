package com.kairos.service.country;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.Currency;
import com.kairos.persistence.model.country.default_data.CurrencyDTO;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CurrencyGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_COUNTRY_ID_NOTFOUND;

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
    @Inject
    private ExceptionService exceptionService;


    public CurrencyDTO createCurrency(long countryId, CurrencyDTO currencyDTO) {
        Boolean currencyExistInCountryByNameOrCode = currencyGraphRepository.currencyExistInCountryByNameOrCode(countryId, "(?i)" + currencyDTO.getName(), currencyDTO.getCurrencyCode(), -1L);
        if (currencyExistInCountryByNameOrCode) {
            exceptionService.duplicateDataException("errror.currency.name.code.exist");
        }
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        }
        Currency currency = new Currency(currencyDTO.getName(), currencyDTO.getDescription(), currencyDTO.getCurrencyCode());
        currency.setCountry(country);
        currencyGraphRepository.save(currency);
        currencyDTO.setId(currency.getId());
        return currencyDTO;
    }

    public List<CurrencyDTO> getCurrencies(long countryId) {
        List<Currency> currencies = currencyGraphRepository.findCurrencyByCountry(countryId);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(currencies, CurrencyDTO.class);
    }

    public CurrencyDTO updateCurrency(long countryId, CurrencyDTO currencyDTO) {
        Boolean currencyExistInCountryByNameOrCode = currencyGraphRepository.currencyExistInCountryByNameOrCode(countryId, "(?i)" + currencyDTO.getName(), currencyDTO.getCurrencyCode(), currencyDTO.getId());
        if (currencyExistInCountryByNameOrCode) {
            exceptionService.duplicateDataException("errror.currency.name.code.exist");
        }
        Currency currency = currencyGraphRepository.findOne(currencyDTO.getId());
        if (currency != null) {
            currency.setName(currencyDTO.getName());
            currency.setDescription(currencyDTO.getDescription());
            currency.setCurrencyCode(currencyDTO.getCurrencyCode());
            currencyGraphRepository.save(currency);
        }
        return currencyDTO;
    }

    public boolean deleteCurrency(long currencyId) {
        Currency currency = currencyGraphRepository.findOne(currencyId);
        if (currency != null) {
            currency.setDeleted(true);
            currencyGraphRepository.save(currency);
        } else {
            exceptionService.dataNotFoundByIdException("error.currency.notfound");
        }
        return true;
    }

    public Currency getCurrencyByCountryId(Long countryId){
       return  currencyGraphRepository.findFirstByCountryIdAndDeletedFalse(countryId);
    }
}
