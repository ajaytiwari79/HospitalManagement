package com.kairos.dto.user.country.system_setting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemLanguageDTO {

    private Long id;
    private String name;
    private String code;
    private boolean active;
    private boolean defaultLanguage;
    private boolean selected;

}
