package com.kairos.service.language;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.user.language.Language;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.FormatUtil;
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
public class LanguageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private LanguageGraphRepository languageGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public List<Language> getAllLanguage(){
        return languageGraphRepository.findAll();
    }
// TODO FIX LATER
    public Language save( Language language){
        return languageGraphRepository.save(language);
    }
    public List<Language> getLanguageByCountryId(long countryId){
        return languageGraphRepository.getLanguageByCountryId(countryId);
    }



    public Map<String, Object> createLanguage(long countryId, Language language){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){

            Boolean languageExistInCountryByName = languageGraphRepository.languageExistInCountryByName(countryId, "(?i)" + language.getName(), -1L);
            if (languageExistInCountryByName) {
                exceptionService.duplicateDataException("error.Language.name.exist");
            }

            language.setCountry(country);
            languageGraphRepository.save(language);
            return language.retrieveDetails();
        }
        return  null;
    }
    public Map<String, Object> updateLanguage(Language language, long countryId){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){

            Boolean languageExistInCountryByName = languageGraphRepository.languageExistInCountryByName(countryId, "(?i)" + language.getName(), language.getId());
            if (languageExistInCountryByName) {
                exceptionService.duplicateDataException("error.Language.name.exist");
            }

            Language currentLanguage = languageGraphRepository.findOne(language.getId());
            currentLanguage.setDescription(language.getDescription());
            currentLanguage.setName(language.getName());
            languageGraphRepository.save(currentLanguage);
            return currentLanguage.retrieveDetails();
        }
        return  null;
    }

    public Language deleteLanguage(long languageId){
        Language currentLanguage = languageGraphRepository.findOne(languageId);
        if (currentLanguage!=null){
            currentLanguage.setEnabled(false);
            languageGraphRepository.save(currentLanguage);
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
