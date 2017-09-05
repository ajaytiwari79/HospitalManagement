package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE10
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumDaysOffInPeriodWTATemplate extends WTABaseRuleTemplate {

    private List<String> balanceType;//multiple check boxes
    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
    private long daysLimit;


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

    public MaximumDaysOffInPeriodWTATemplate(String name, String templateType, boolean isActive,
                                             String description, List<String> balanceType, long intervalLength, long validationStartDateMillis, long minimumDaysOff, String intervalUnit) {
        this.intervalLength = intervalLength;
        this.balanceType = balanceType;
        this.daysLimit = minimumDaysOff;
        this.validationStartDateMillis = validationStartDateMillis;

        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;
        this.intervalUnit = intervalUnit;

    }

    public MaximumDaysOffInPeriodWTATemplate() {
    }
}
