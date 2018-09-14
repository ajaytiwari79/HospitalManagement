package com.kairos.shiftplanning.domain.wta.updated_wta;

import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.enums.wta.WTATemplateType;
import org.springframework.beans.BeanUtils;

import java.math.BigInteger;
import java.util.List;


/**
 * Created by vipul on 26/7/17.
 */

//@Document(collection = "wtaBaseRuleTemplate")
public class WTABaseRuleTemplate{

    protected BigInteger id;
    protected String name;
    protected String description;
    protected boolean disabled;
    protected BigInteger ruleTemplateCategoryId;
    protected String lastUpdatedBy;
    protected Long countryId;
    protected WTATemplateType wtaTemplateType;
    protected List<PhaseTemplateValue> phaseTemplateValues;
    protected Integer staffCanIgnoreCounter;
    protected Integer managementCanIgnoreCounter;

    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }


    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public BigInteger getRuleTemplateCategoryId() {
        return ruleTemplateCategoryId;
    }

    public void setRuleTemplateCategoryId(BigInteger ruleTemplateCategoryId) {
        this.ruleTemplateCategoryId = ruleTemplateCategoryId;
    }

    public WTABaseRuleTemplate(){}

    public WTABaseRuleTemplate(String name, String description) {
        this.name = name;
        this.description = description;
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


    public List<PhaseTemplateValue> getPhaseTemplateValues() {
        return phaseTemplateValues;
    }

    public void setPhaseTemplateValues(List<PhaseTemplateValue> phaseTemplateValues) {
        this.phaseTemplateValues = phaseTemplateValues;
    }
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public static WTABaseRuleTemplate copyProperties(WTABaseRuleTemplate source, WTABaseRuleTemplate target){
        BeanUtils.copyProperties(source,target);
        return target;
    }


    public Integer getStaffCanIgnoreCounter() {
        return staffCanIgnoreCounter;
    }

    public void setStaffCanIgnoreCounter(Integer staffCanIgnoreCounter) {
        this.staffCanIgnoreCounter = staffCanIgnoreCounter;
    }

    public Integer getManagementCanIgnoreCounter() {
        return managementCanIgnoreCounter;
    }

    public void setManagementCanIgnoreCounter(Integer managementCanIgnoreCounter) {
        this.managementCanIgnoreCounter = managementCanIgnoreCounter;
    }
}
