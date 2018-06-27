package com.kairos.dto.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.activity.tabs.ActivityCategory;
import com.kairos.persistence.model.activity.tabs.CTAAndWTASettingsActivityTab;
import com.kairos.persistence.model.activity.tabs.GeneralActivityTab;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by prerna on 15/2/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityWithCTAWTASettingsDTO {
    private BigInteger id;

    private String name;

    private String description;

    private CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab;

    private BigInteger categoryId;

    public ActivityWithCTAWTASettingsDTO(){

    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CTAAndWTASettingsActivityTab getCtaAndWtaSettingsActivityTab() {
        return ctaAndWtaSettingsActivityTab;
    }

    public void setCtaAndWtaSettingsActivityTab(CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab) {
        this.ctaAndWtaSettingsActivityTab = ctaAndWtaSettingsActivityTab;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

}
