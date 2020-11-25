package com.kairos.dto.user.country.system_setting;

import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SystemLanguageDTO {

    private Long id;
    private String name;
    private String code;
    private boolean active;
    private boolean defaultLanguage;
    private boolean selected;
    private Long countryId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations;



}
