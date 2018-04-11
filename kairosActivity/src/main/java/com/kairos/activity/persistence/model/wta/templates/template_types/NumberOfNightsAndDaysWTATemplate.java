package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE9
 */
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NumberOfNightsAndDaysWTATemplate extends WTABaseRuleTemplate {

    private List<String> balanceType;//multiple check boxes
    private long nightsWorked;
    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
    private WTATemplateType wtaTemplateType = WTATemplateType.NumberOfNightsAndDays;


    public WTATemplateType getWtaTemplateType() {
        return wtaTemplateType;
    }

    public void setWtaTemplateType(WTATemplateType wtaTemplateType) {
        this.wtaTemplateType = wtaTemplateType;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(long nightsWorked) {
        this.nightsWorked = nightsWorked;
    }

    public long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(long intervalLength) {
        this.intervalLength = intervalLength;
    }

    public long getValidationStartDateMillis() {
        return validationStartDateMillis;
    }

    public void setValidationStartDateMillis(long validationStartDateMillis) {
        this.validationStartDateMillis = validationStartDateMillis;
    }

    public NumberOfNightsAndDaysWTATemplate(String name, String templateType, boolean disabled, String description, List<String> balanceType, long nightsWorked, long intervalLength, long validationStartDateMillis, String intervalUnit) {
        this.nightsWorked = nightsWorked;
        this.balanceType = balanceType;
        this.intervalLength =intervalLength;
        this.validationStartDateMillis =validationStartDateMillis;;
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;
        this.intervalUnit = intervalUnit;
    }
    public NumberOfNightsAndDaysWTATemplate() {

    }



}
