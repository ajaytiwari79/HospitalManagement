package com.kairos.dto.user.country;

import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user_context.UserContext;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Properties;

import java.util.HashMap;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;

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

    public String getName() {
        if(isNotNull(translatedNames.get(UserContext.getUserDetails().getLanguage().toLowerCase())) && !translatedNames.get(UserContext.getUserDetails().getLanguage().toLowerCase()).equals("")) {
            return translatedNames.getOrDefault(UserContext.getUserDetails().getLanguage().toLowerCase(), name);
        }else {
            return name;
        }
    }

    public String getDescription() {
        if(isNotNull(translatedDescriptions.get(UserContext.getUserDetails().getLanguage().toLowerCase())) && !translatedDescriptions.get(UserContext.getUserDetails().getLanguage().toLowerCase()).equals("")) {
            return translatedDescriptions.getOrDefault(UserContext.getUserDetails().getLanguage().toLowerCase(), description);
        }else{
            return description;
        }
    }


}
