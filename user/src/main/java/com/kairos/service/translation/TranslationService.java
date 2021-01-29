package com.kairos.service.translation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.repository.custom_repository.CommonRepository;
import com.kairos.persistence.repository.system_setting.SystemLanguageGraphRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

@Service
public class TranslationService {
    @Inject
    private SystemLanguageGraphRepository systemLanguageGraphRepository;

    @CacheEvict(value = {"getPermission","generateHierarchy","getTabHierarchy"}, allEntries = true)
    public Map<String, TranslationInfo> updateTranslation(Long id, Map<String, TranslationInfo> translation){
        String jsonString;
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            jsonString = objectMapper.writeValueAsString(translation);
        }catch (Exception ex) {
            jsonString = "";
        }
        systemLanguageGraphRepository.updateTranslation(id, jsonString);
        return translation;
    }

}
