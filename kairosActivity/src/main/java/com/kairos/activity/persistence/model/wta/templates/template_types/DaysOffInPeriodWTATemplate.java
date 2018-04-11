package com.kairos.activity.persistence.model.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.activity.persistence.enums.WTATemplateType;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE10
 */
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaysOffInPeriodWTATemplate extends WTABaseRuleTemplate {

    private List<String> balanceType;//multiple check boxes
    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
    private long daysLimit;
    private WTATemplateType wtaTemplateType = WTATemplateType.DaysOffInPeriod;


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

    public long getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(long daysLimit) {
        this.daysLimit = daysLimit;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
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

    public DaysOffInPeriodWTATemplate(String name, String templateType, boolean disabled,
                                      String description, List<String> balanceType, long intervalLength, long validationStartDateMillis, long minimumDaysOff, String intervalUnit) {
        this.intervalLength = intervalLength;
        this.balanceType = balanceType;
        this.daysLimit = minimumDaysOff;
        this.validationStartDateMillis = validationStartDateMillis;

        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;
        this.intervalUnit = intervalUnit;

    }

    public DaysOffInPeriodWTATemplate() {
    }
}
