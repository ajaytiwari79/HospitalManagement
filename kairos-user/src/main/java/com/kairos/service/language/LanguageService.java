package com.kairos.service.language;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 28/11/16.
 */
@Service
@Transactional
public class LanguageService extends UserBaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private LanguageGraphRepository languageGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    public List<Language> getAllLanguage(){
        return languageGraphRepository.findAll();
    }

    public List<Language> getLanguageByCountryId(long countryId){
        return languageGraphRepository.getLanguageByCountryId(countryId);
    }



    public Map<String, Object> createLanguage(long countryId, Language language){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            language.setCountry(country);
            save(language);
            return language.retrieveDetails();
        }
        return  null;
    }
    public Map<String, Object> updateLanguage(Language language, long countryId){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            Language currentLanguage = languageGraphRepository.findOne(language.getId());
            currentLanguage.setDescription(language.getDescription());
            currentLanguage.setName(language.getName());
            save(currentLanguage);
            return currentLanguage.retrieveDetails();
        }
        return  null;
    }

    public Language deleteLanguage(long languageId){
        Language currentLanguage = languageGraphRepository.findOne(languageId);
        if (currentLanguage!=null){
            currentLanguage.setEnabled(false);
            save(currentLanguage);
            return currentLanguage;
        }
        return  null;
    }

    public List<Map<String, Object>> getUnitAvailableLanguages(long unitId) {

        Long countryId = countryGraphRepository.getCountryIdByUnitId(unitId);

        if (countryId != null) {

            logger.info("Finding available languages for CountryId: " + countryId);
            List<Map<String, Object>> data = languageGraphRepository.getLanguageByCountryIdAnotherFormat(countryId);
            return data != null ? FormatUtil.formatNeoResponse(data) : null;

        }
        return null;
    }




}
