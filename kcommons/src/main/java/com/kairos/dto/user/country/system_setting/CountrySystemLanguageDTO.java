package com.kairos.dto.user.country.system_setting;

import java.util.List;

public class CountrySystemLanguageDTO {
    List<SystemLanguageDTO> systemLanguages;
    List<SystemLanguageDTO> selectedLanguages;

    public CountrySystemLanguageDTO(List<SystemLanguageDTO> systemLanguage, List<SystemLanguageDTO> selectedLanguage) {
        this.systemLanguages = systemLanguage;
        this.selectedLanguages = selectedLanguage;

    }

    public List<SystemLanguageDTO> getSelectedLanguages() {
        return selectedLanguages;
    }

    public void setSelectedLanguages(List<SystemLanguageDTO> selectedLanguages) {
        this.selectedLanguages = selectedLanguages;
    }

    public List<SystemLanguageDTO> getSystemLanguages() {

        return systemLanguages;
    }

    public void setSystemLanguages(List<SystemLanguageDTO> systemLanguages) {
        this.systemLanguages = systemLanguages;
    }
}
