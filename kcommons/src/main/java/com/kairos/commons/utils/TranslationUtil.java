package com.kairos.commons.utils;

import com.kairos.dto.TranslationInfo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


public class TranslationUtil {

    public static void updateTranslationData(Map<String, TranslationInfo> translations,Map<String,String> translatedNames,Map<String,String> translatedDescriptios){
        for(Map.Entry<String,TranslationInfo> entry :translations.entrySet()){
            translatedNames.put(entry.getKey(),entry.getValue().getName());
            translatedDescriptios.put(entry.getKey(),entry.getValue().getDescription());
        }
    }

}
