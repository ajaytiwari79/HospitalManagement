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
public class WtaTemplate19 extends WTABaseRuleTemplate {
    private List<String> balanceType;//multiple check boxes
    private long interval;//
    private String intervalUnit;
    private long validationStartDate;

    private long number;
    private boolean onlyCompositeShifts;//(checkbox)

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

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public boolean isOnlyCompositeShifts() {
        return onlyCompositeShifts;
    }

    public void setOnlyCompositeShifts(boolean onlyCompositeShifts) {
        this.onlyCompositeShifts = onlyCompositeShifts;
    }

    public WtaTemplate19(String name, String templateType,  boolean isActive,
                         String description, List<String> balanceType, long interval, String intervalUnit,
                         long validationStartDate, long number, boolean onlyCompositeShifts) {
        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;
        this.balanceType = balanceType;
        this.interval=interval;
        this.intervalUnit=intervalUnit;
        this.validationStartDate=validationStartDate;
        this.number=number;
        this.onlyCompositeShifts=onlyCompositeShifts;

    }
    public WtaTemplate19() {
    }
}
