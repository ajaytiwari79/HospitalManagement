package com.kairos.dto.user.country;

import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class LanguageDTO {
    private Long id;
    private String name;
    private String description;
    private boolean inactive;
    private boolean isEnabled = true;
    private long readLevel;
    private long writeLevel;
    private long speakLevel;
    private Long countryId;

    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations ;

}
