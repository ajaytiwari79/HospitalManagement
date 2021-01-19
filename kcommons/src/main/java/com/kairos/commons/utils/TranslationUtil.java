package com.kairos.commons.utils;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user_context.UserContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isMapEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TranslationUtil {

    public static final String NAME = "name";
    public static final String TRANSLATIONS = "translations";
    public static final String DESCRIPTION = "description";

    //TODO remove uses of this function
    public static void convertTranslationFromStringToMap(Map<String, Object> map) {
        Map<String, TranslationInfo> translations;
        try{
            Map<String,Object> translatedMap = ObjectMapperUtils.mapper.readValue(map.get(TRANSLATIONS).toString(), Map.class);
            Map<String, TranslationInfo> finalTranslations = new HashMap<>();
            translatedMap.forEach((k, v) -> finalTranslations.put(k, new TranslationInfo(((Map) v).get(NAME).toString(), ((Map) v).get(DESCRIPTION).toString())));
            translations = finalTranslations;
        }catch (Exception ex) {
            translations = new HashMap<>();
        }
        try{
            map.put(TRANSLATIONS, translations);
        }catch (UnsupportedOperationException e){
            map = new HashMap<>(map);
            map.put(TRANSLATIONS, translations);
        }
        if(isMapEmpty(translations))return;
        if(map.containsKey(NAME)){
            map.put(NAME,getName(translations,map.get(NAME).toString()));
        }
        if(map.containsKey(DESCRIPTION)){
            map.put(DESCRIPTION,getDescription(translations,map.get(DESCRIPTION).toString()));
        }
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
                TranslationInfo translationInfo = new TranslationInfo((String) translationInfoEntry.getValue().get(NAME), (String) translationInfoEntry.getValue().get(DESCRIPTION));
                translationMap.put(translationInfoEntry.getKey(), translationInfo);
            }
            return translationMap;
        }else{
            return new HashMap<>();
        }
    }

    public static Map<String, TranslationInfo> getData(String translatedData) {
        return ObjectMapperUtils.jsonStringToObject(translatedData,Map.class);
    }
}
