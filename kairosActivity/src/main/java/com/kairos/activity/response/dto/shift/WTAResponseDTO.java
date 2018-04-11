package com.kairos.activity.response.dto.shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by vipul on 29/1/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WTAResponseDTO {
    private List<RuleTemplateCategoryDTO> ruleTemplates;
    private Long startDateMillis;
    private Long endDateMillis;
    private Long expiryDate;
    private String name;
    private String description;
    private long id;

    public WTAResponseDTO() {
    }

    public List<RuleTemplateCategoryDTO> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<RuleTemplateCategoryDTO> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}