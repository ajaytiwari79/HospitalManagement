package com.kairos.response.dto.web.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityTypeDTO {
    private Long id;
    private String name;
    private CTAAndWTASettingsActivityTabDTO ctaAndWtaSettingsActivityTab;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CTAAndWTASettingsActivityTabDTO getCtaAndWtaSettingsActivityTab() {
        return ctaAndWtaSettingsActivityTab;
    }

    public void setCtaAndWtaSettingsActivityTab(CTAAndWTASettingsActivityTabDTO ctaAndWtaSettingsActivityTab) {
        this.ctaAndWtaSettingsActivityTab = ctaAndWtaSettingsActivityTab;
    }
}
