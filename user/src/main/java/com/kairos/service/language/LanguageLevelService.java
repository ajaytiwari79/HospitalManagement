package com.kairos.service.language;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.user.language.LanguageLevel;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageLevelGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 28/11/16.
 */
@Service
@Transactional
public class LanguageLevelService {

    @Inject
    private LanguageLevelGraphRepository languageLevelGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public LanguageLevel addNewLanguageLevel(LanguageLevel languageLevel) {
        languageLevelGraphRepository.save(languageLevel);
        return languageLevel;
    }

    public List<LanguageLevel> getAllLanguageLevel() {
        return languageLevelGraphRepository.findAll();
    }

    public List<Object> getLanguageLevelByCountryId(long countryId) {
        List<Map<String, Object>> data = languageLevelGraphRepository.getLanguageLevelByCountryId(countryId);
        List<Object> objectList=new ArrayList<>();
        if (data != null) {
            for (Map<String, Object> map : data) {
                Object o = map.get("result");
                objectList.add(o);
            }
        }
        return objectList;
    }


    public Map<String, Object> createLanguageLevel(long countryId, LanguageLevel languageLevel) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country != null) {

            boolean languageLevelExistInCountryByName = languageLevelGraphRepository.languageLevelExistInCountryByName(countryId, "(?i)" + languageLevel.getName(), -1L);
            if (languageLevelExistInCountryByName) {
                exceptionService.duplicateDataException("error.LanguageLevel.name.exist");
            }

            languageLevel.setCountry(country);
            languageLevelGraphRepository.save(languageLevel);
            return languageLevel.retrieveDetails();
        }
        return null;
    }

    public Map<String, Object> updateLanguageLevel(LanguageLevel languageLevel, long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country != null) {

            boolean languageLevelExistInCountryByName = languageLevelGraphRepository.languageLevelExistInCountryByName(countryId, "(?i)" + languageLevel.getName(), languageLevel.getId());
            if (languageLevelExistInCountryByName) {
                exceptionService.duplicateDataException("error.LanguageLevel.name.exist");
            }

            LanguageLevel currentLanguageLevel = languageLevelGraphRepository.findOne(languageLevel.getId());
            currentLanguageLevel.setDescription(languageLevel.getDescription());
            currentLanguageLevel.setName(languageLevel.getName());
            languageLevelGraphRepository.save(currentLanguageLevel);
            return currentLanguageLevel.retrieveDetails();
        }
        return null;
    }

    public LanguageLevel deleteLanguageLevel(long languageLevelId) {
        LanguageLevel languageLevel = languageLevelGraphRepository.findOne(languageLevelId);
        if (languageLevel != null) {
            languageLevel.setEnabled(false);
            languageLevelGraphRepository.save(languageLevel);
            return languageLevel;
        }
        return null;
    }


}
