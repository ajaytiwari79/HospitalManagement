package com.kairos.commons.utils;

import com.kairos.dto.TranslationInfo;

import java.util.HashMap;
import java.util.Map;


public class TranslationUtil {

    public static void updateTranslationData(Map<String, TranslationInfo> translations,Map<String,String> translatedNames,Map<String,String> translatedDescriptios){
        for(Map.Entry<String,TranslationInfo> entry :translations.entrySet()){
            translatedNames.put(entry.getKey(),entry.getValue().getName());
            translatedDescriptios.put(entry.getKey(),entry.getValue().getDescription());
        }
    }

    public static Map<String, TranslationInfo> getTranslatedData(Map<String,String> translatedNames,Map<String,String> translatedDescriptions) {
        Map<String, TranslationInfo> infoMap=new HashMap<>();
        translatedNames.forEach((k,v)-> infoMap.put(k,new TranslationInfo(v,translatedDescriptions.get(k))));
        return infoMap;
    }

}
