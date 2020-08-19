package com.kairos.dto.user.country;

import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Properties;

import java.util.HashMap;
import java.util.Map;
@Getter
@Setter
public class LevelDTO {
    private Long id;
    private String name;
    private String description;
    private boolean isEnabled = true;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations ;

    public Map<String, TranslationInfo> getTranslatedData() {
        Map<String, TranslationInfo> infoMap=new HashMap<>();
        translatedNames.forEach((k,v)-> infoMap.put(k,new TranslationInfo(v,translatedDescriptions.get(k))));
        return infoMap;
    }
}