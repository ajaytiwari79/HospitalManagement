package com.kairos.commons.utils;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.system_setting.SystemLanguageDTO;
import com.kairos.dto.user_context.UserContext;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;


public class TranslationUtil {

    public static void updateTranslationData(Map<String, TranslationInfo> translations, Map<String, String> translatedNames, Map<String, String> translatedDescriptios) {
        for (Map.Entry<String, TranslationInfo> entry : translations.entrySet()) {
            translatedNames.put(entry.getKey(), entry.getValue().getName());
            translatedDescriptios.put(entry.getKey(), entry.getValue().getDescription());
        }
    }

    public static Map<String, TranslationInfo> getTranslatedData(Map<String, String> translatedNames, Map<String, String> translatedDescriptions) {
        Map<String, TranslationInfo> infoMap = new HashMap<>();
        translatedNames.forEach((k, v) -> infoMap.put(k, new TranslationInfo(v, translatedDescriptions.get(k))));
        return infoMap;
    }

    public static String getName(Map<String, TranslationInfo> translations, String name) {
        Boolean isNullOrEmptyString = isNotNull(translations) && isNotNull(translations.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase())) && !StringUtils.isEmpty(translations.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase()).getName());
        if(isNullOrEmptyString) {
            return translations.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase()).getName();
        }else {
            return name;
        }
    }

    public static String getDescription(Map<String, TranslationInfo> translations, String description) {
        Boolean isNullOrEmptyString = isNotNull(translations) && isNotNull(translations.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase())) && !StringUtils.isEmpty(translations.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase()).getDescription());
        if(isNullOrEmptyString) {
            return translations.get(UserContext.getUserDetails().getUserLanguage().getName().toLowerCase()).getDescription();
        }else {
            return description;
        }
    }

    public static Map<String,TranslationInfo> convertUnmodifiableMapToModifiableMap(Map<String, TranslationInfo> translations){
        Map<String,Map> translationInfoMap = ObjectMapperUtils.copyPropertiesByMapper(translations, HashedMap.class);
        Map<String,TranslationInfo> translationMap = new HashMap<>();
        for(Map.Entry<String,Map> translationInfoEntry : translationInfoMap.entrySet()){
            TranslationInfo translationInfo = new TranslationInfo((String)translationInfoEntry.getValue().get("name"),(String)translationInfoEntry.getValue().get("description"));
            translationMap.put(translationInfoEntry.getKey(),translationInfo);
        }
        return translationMap;
    }

}
