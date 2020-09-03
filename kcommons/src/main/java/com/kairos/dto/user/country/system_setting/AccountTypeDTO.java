package com.kairos.dto.user.country.system_setting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;

//  Created By vipul   On 10/8/18
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AccountTypeDTO {
    private Long id;
    @NotNull
    private String name;
    private String description;
    private Long countryId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations ;


}
