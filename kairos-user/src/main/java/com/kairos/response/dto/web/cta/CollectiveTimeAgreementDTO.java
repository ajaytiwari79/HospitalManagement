package com.kairos.response.dto.web.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectiveTimeAgreementDTO {
    private String name;
    private String description;
    private Long expertise;
    private List<Long> organizationTypeList=new ArrayList<>();
    private List<Long> ruleTemplates=new ArrayList<>();
    private boolean disabled;

    public CollectiveTimeAgreementDTO() {

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

    public Long getExpertise() {
        return expertise;
    }

    public void setExpertise(Long expertise) {
        this.expertise = expertise;
    }

    public List<Long> getOrganizationTypeList() {
        return organizationTypeList;
    }

    public void setOrganizationTypeList(List<Long> organizationTypeList) {
        this.organizationTypeList = organizationTypeList;
    }

    public List<Long> getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List<Long> ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
