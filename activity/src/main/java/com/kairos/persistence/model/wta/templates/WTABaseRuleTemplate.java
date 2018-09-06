package com.kairos.persistence.model.wta.templates;

import com.kairos.activity.wta.templates.PhaseTemplateValue;
import com.kairos.enums.WTATemplateType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;


/**
 * Created by vipul on 26/7/17.
 */

@Document(collection = "wtaBaseRuleTemplate")
public class WTABaseRuleTemplate extends MongoBaseEntity{

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

    public WTABaseRuleTemplate(String name,String description) {
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

    public void validateRules(RuleTemplateSpecificInfo infoWrapper){}

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
