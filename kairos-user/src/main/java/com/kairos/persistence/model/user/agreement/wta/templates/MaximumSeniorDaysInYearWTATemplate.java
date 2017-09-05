package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by pawanmandhan on 5/8/17.
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumSeniorDaysInYearWTATemplate extends WTABaseRuleTemplate {

    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
    private long daysLimit;
    private String activityCode;

    public long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(long intervalLength) {
        this.intervalLength = intervalLength;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public long getValidationStartDateMillis() {
        return validationStartDateMillis;
    }

    public void setValidationStartDateMillis(long validationStartDateMillis) {
        this.validationStartDateMillis = validationStartDateMillis;
    }

    public long getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(long daysLimit) {
        this.daysLimit = daysLimit;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public MaximumSeniorDaysInYearWTATemplate(String name, String templateType, boolean isActive,
                                              String description, long intervalLength, String intervalUnit, long validationStartDateMillis,
                                              long daysLimit, String activityCode) {
        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;
        this.intervalLength =intervalLength;
        this.intervalUnit=intervalUnit;
        this.validationStartDateMillis =validationStartDateMillis;
        this.daysLimit =daysLimit;
        this.activityCode=activityCode;

    }
    public MaximumSeniorDaysInYearWTATemplate() {
    }


}
