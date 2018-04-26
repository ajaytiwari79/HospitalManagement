package com.kairos.activity.persistence.model.wta.templates;

import com.kairos.activity.persistence.enums.PartOfDay;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;
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
    protected BigInteger WTARuleTemplateCategoryId;
    protected String lastUpdatedBy;
    protected Long countryId;

    protected List<PhaseTemplateValue> phaseTemplateValues;



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

    public BigInteger getWTARuleTemplateCategoryId() {
        return WTARuleTemplateCategoryId;
    }

    public void setWTARuleTemplateCategoryId(BigInteger WTARuleTemplateCategoryId) {
        this.WTARuleTemplateCategoryId = WTARuleTemplateCategoryId;
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

}
