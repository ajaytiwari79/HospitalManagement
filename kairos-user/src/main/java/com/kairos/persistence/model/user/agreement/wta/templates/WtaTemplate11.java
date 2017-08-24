package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 */@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WtaTemplate11 extends WTABaseRuleTemplate {

    private List<String> balanceType;//multiple check boxes
    private long interval;
    private long validationStartDate;
    private long minimumDaysOff;
    private boolean balanceAdjustment;
    private boolean calculatedShift;
    private String maximumAvgTime;
    public List<String> getBalanceType() {
        return balanceType;
    }

    public long getMinimumDaysOff() {
        return minimumDaysOff;
    }

    public void setMinimumDaysOff(long minimumDaysOff) {
        this.minimumDaysOff = minimumDaysOff;
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

    public String getMaximumAvgTime() {
        return maximumAvgTime;
    }

    public void setMaximumAvgTime(String maximumAvgTime) {
        this.maximumAvgTime = maximumAvgTime;
    }

    public long getValidationStartDate() {
        return validationStartDate;
    }


    public boolean isBalanceAdjustment() {
        return balanceAdjustment;
    }

    public void setBalanceAdjustment(boolean balanceAdjustment) {
        this.balanceAdjustment = balanceAdjustment;
    }

    public void setValidationStartDate(long validationStartDate) {
        this.validationStartDate = validationStartDate;
    }


    public boolean isCalculatedShift() {
        return calculatedShift;
    }

    public void setCalculatedShift(boolean calculatedShift) {
        this.calculatedShift = calculatedShift;
    }

    public WtaTemplate11(String name, String templateType,  boolean isActive,
                         String description, List<String> balanceType, long interval, long validationStartDate, long minimumDaysOff
            , boolean balanceAdjustment,boolean calculatedShift,String maximumAvgTime) {
        this.interval = interval;
        this.balanceType = balanceType;
        this.minimumDaysOff = minimumDaysOff;
        this.validationStartDate = validationStartDate;
        this.name = name;
        this.templateType = templateType;
       this.isActive = isActive;
        this.description = description;
        this.balanceAdjustment=balanceAdjustment;
        this.calculatedShift=calculatedShift;
        this.maximumAvgTime=maximumAvgTime;

    }
    public WtaTemplate11() {
    }



}
