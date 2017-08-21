package com.kairos.service.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.language.LanguageLevel;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.language.LanguageLevelGraphRepository;
import com.kairos.service.UserBaseService;

/**
 * Created by prabjot on 28/11/16.
 */
@Service
@Transactional
public class LanguageLevelService extends UserBaseService {

    @Inject
    private LanguageLevelGraphRepository languageLevelGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    public LanguageLevel addNewLanguageLevel(LanguageLevel languageLevel) {
        save(languageLevel);
        return languageLevel;
    }

    public List<LanguageLevel> getAllLanguageLevel() {
        return languageLevelGraphRepository.findAll();
    }

    public List<Object> getLanguageLevelByCountryId(long countryId) {
        List<Map<String, Object>> data = languageLevelGraphRepository.getLanguageLevelByCountryId(countryId);
        List<Object> objectList;
        if (data != null) {
            objectList = new ArrayList<>();
            for (Map<String, Object> map : data) {
                Object o = map.get("result");
                objectList.add(o);
            }
            return objectList;
        }
        return null;
    }


    public Map<String, Object> createLanguageLevel(long countryId, LanguageLevel languageLevel) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country != null) {
            languageLevel.setCountry(country);
            save(languageLevel);
            return languageLevel.retrieveDetails();
        }
        return null;
    }

    public Map<String, Object> updateLanguageLevel(LanguageLevel languageLevel, long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country != null) {
            LanguageLevel currentLanguageLevel = languageLevelGraphRepository.findOne(languageLevel.getId());
            currentLanguageLevel.setDescription(languageLevel.getDescription());
            currentLanguageLevel.setName(languageLevel.getName());
            save(currentLanguageLevel);
            return currentLanguageLevel.retrieveDetails();
        }
        return null;
    }

    public LanguageLevel deleteLanguageLevel(long languageLevelId) {
        LanguageLevel languageLevel = languageLevelGraphRepository.findOne(languageLevelId);
        if (languageLevel != null) {
            languageLevel.setEnabled(false);
            save(languageLevel);
            return languageLevel;
        }
        return null;
    }


}
