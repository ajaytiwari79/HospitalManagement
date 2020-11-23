package com.kairos.commons.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user_context.UserContext;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;


public class TranslationUtil {

    public static void convertTranslationFromStringToMap(Map<String, Object> map) {
        Map<String, TranslationInfo> translations;
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            translations = objectMapper.readValue(map.get("translations").toString(), Map.class);
        }catch (Exception ex) {
            translations = new HashMap();
        }
        map.put("translations", translations);
    }

    public static String getName(Map<String, TranslationInfo> translations, String name) {
        boolean isNullOrEmptyString = isNotNull(translations) && isNotNull(translations.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase())) && !StringUtils.isEmpty(translations.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase()).getName().trim());
        if(isNullOrEmptyString) {
            return translations.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase()).getName();
        }else {
            return name;
        }
    }

    public static String getDescription(Map<String, TranslationInfo> translations, String description) {
        boolean isNullOrEmptyString = isNotNull(translations) && isNotNull(translations.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase())) && !StringUtils.isEmpty(translations.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase()).getDescription().trim());
        if(isNullOrEmptyString) {
            return translations.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase()).getDescription();
        }else {
            return description;
        }
    }

    public static Map<String,TranslationInfo> convertUnmodifiableMapToModifiableMap(Map<String, TranslationInfo> translations) {
        if (isNotNull(translations)) {
            Map<String, Map> translationInfoMap = ObjectMapperUtils.copyPropertiesByMapper(translations, HashedMap.class);
            Map<String, TranslationInfo> translationMap = new HashMap<>();
            for (Map.Entry<String, Map> translationInfoEntry : translationInfoMap.entrySet()) {
                TranslationInfo translationInfo = new TranslationInfo((String) translationInfoEntry.getValue().get("name"), (String) translationInfoEntry.getValue().get("description"));
                translationMap.put(translationInfoEntry.getKey(), translationInfo);
            }
            return translationMap;
        }else{
            return new HashMap<>();
        }
    }

}
