package com.kairos.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TranslationDTO {
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
}
