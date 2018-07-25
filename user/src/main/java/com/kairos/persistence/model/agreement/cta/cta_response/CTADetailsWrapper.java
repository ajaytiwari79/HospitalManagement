package com.kairos.persistence.model.agreement.cta.cta_response;

import com.kairos.persistence.model.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.expertise.Expertise;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CTADetailsWrapper {
    private Boolean all;
    private Country country;
    private Expertise expertise;
    private OrganizationType organizationType;
    private OrganizationType organizationSubType;
    private Map<Long,EmploymentType> employmentTypeIdMap;
    private Map<Long,RuleTemplateCategory> ruleTemplateCategoryIdMap;
    private List<Long> selectedRuleTemplateIds= new ArrayList<>();

    public Boolean getAll() {
        return all;
    }

    public void setAll(Boolean all) {
        this.all = all;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public OrganizationType getOrganizationSubType() {
        return organizationSubType;
    }

    public void setOrganizationSubType(OrganizationType organizationSubType) {
        this.organizationSubType = organizationSubType;
    }

    public Map<Long, EmploymentType> getEmploymentTypeIdMap() {
        return employmentTypeIdMap;
    }

    public void setEmploymentTypeIdMap(Map<Long, EmploymentType> employmentTypeIdMap) {
        this.employmentTypeIdMap = employmentTypeIdMap;
    }

    public Map<Long, RuleTemplateCategory> getRuleTemplateCategoryIdMap() {
        return ruleTemplateCategoryIdMap;
    }

    public void setRuleTemplateCategoryIdMap(Map<Long, RuleTemplateCategory> ruleTemplateCategoryIdMap) {
        this.ruleTemplateCategoryIdMap = ruleTemplateCategoryIdMap;
    }

    public List<Long> getSelectedRuleTemplateIds() {
        return selectedRuleTemplateIds;
    }

    public void setSelectedRuleTemplateIds(List<Long> selectedRuleTemplateIds) {
        this.selectedRuleTemplateIds = selectedRuleTemplateIds;
    }
}
