package com.kairos.activity.persistence.model.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.persistence.enums.PartOfDay;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;


/**
 * Created by vipul on 26/7/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "WtaRuleTemplate")
public class WTABaseRuleTemplate extends MongoBaseEntity{

    protected String name;
    protected String description;
    protected boolean disabled;
    protected BigInteger WTARuleTemplateCategory;
    protected List<PartOfDay> partOfDays;
    protected Long countryId;
    protected int lastInsertedValue;

    protected List<PhaseTemplateValue> phaseTemplateValues;
    protected int recommendedValue;
    protected String lastUpdatedBy;
    protected boolean minimum;


    public int getLastInsertedValue() {
        return lastInsertedValue;
    }

    public void setLastInsertedValue(int lastInsertedValue) {
        this.lastInsertedValue = lastInsertedValue;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public boolean isMinimum() {
        return minimum;
    }

    public void setMinimum(boolean minimum) {
        this.minimum = minimum;
    }

    public List<PartOfDay> getPartOfDays() {
        return partOfDays;
    }

    public void setPartOfDays(List<PartOfDay> partOfDays) {
        this.partOfDays = partOfDays;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public BigInteger getWTARuleTemplateCategory() {
        return WTARuleTemplateCategory;
    }

    public void setWTARuleTemplateCategory(BigInteger WTARuleTemplateCategory) {
        this.WTARuleTemplateCategory = WTARuleTemplateCategory;
    }

    public WTABaseRuleTemplate(){}

    public WTABaseRuleTemplate(String name, boolean minimum, String description) {
        this.name = name;
        this.minimum = minimum;
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
    public int getRecommendedValue() {
        return recommendedValue;
    }

    public void setRecommendedValue(int recommendedValue) {
        this.recommendedValue = recommendedValue;
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
