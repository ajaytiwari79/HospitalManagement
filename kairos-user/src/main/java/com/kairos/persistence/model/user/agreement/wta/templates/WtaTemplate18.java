package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WtaTemplate18 extends WTABaseRuleTemplate {
    private List<String> balanceType;//multiple check boxes
    private long interval;//
    private String intervalUnit;
    private long validationStartDate;
    private String continuousDayRestHours;
    private String averageRest;//(hours number)
    private String shiftAffiliation;//(List checkbox)

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

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

    public String getContinuousDayRestHours() {
        return continuousDayRestHours;
    }

    public void setContinuousDayRestHours(String continuousDayRestHours) {
        this.continuousDayRestHours = continuousDayRestHours;
    }

    public String getAverageRest() {
        return averageRest;
    }

    public void setAverageRest(String averageRest) {
        this.averageRest = averageRest;
    }

    public String getShiftAffiliation() {
        return shiftAffiliation;
    }

    public void setShiftAffiliation(String shiftAffiliation) {
        this.shiftAffiliation = shiftAffiliation;
    }

    public WtaTemplate18(String name, String templateType, boolean isActive,
                         String description, List<String> balanceType, long interval, String intervalUnit, long validationStartDate,
                         String continuousDayRestHours, String averageRest, String shiftAffiliation) {
        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;
        this.balanceType = balanceType;
        this.interval=interval;
        this.intervalUnit=intervalUnit;
        this.validationStartDate=validationStartDate;
        this.continuousDayRestHours=continuousDayRestHours;
        this.averageRest=averageRest;
        this.shiftAffiliation=shiftAffiliation;
    }
    public WtaTemplate18() {
    }

}
