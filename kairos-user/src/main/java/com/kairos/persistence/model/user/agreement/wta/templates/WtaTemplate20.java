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
public class WtaTemplate20 extends WTABaseRuleTemplate {

    private long interval;//
    private String intervalUnit;
    private long validationStartDate;
    private long number;
    private String activityCode;// checkbox)

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public long getValidationStartDate() {
        return validationStartDate;
    }

    public void setValidationStartDate(long validationStartDate) {
        this.validationStartDate = validationStartDate;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public WtaTemplate20(String name, String templateType, boolean isActive,
                         String description, long interval, String intervalUnit, long validationStartDate,
                         long number, String activityCode) {
        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;
        this.interval=interval;
        this.intervalUnit=intervalUnit;
        this.validationStartDate=validationStartDate;
        this.number=number;
        this.activityCode=activityCode;

    }
    public WtaTemplate20() {
    }


}
