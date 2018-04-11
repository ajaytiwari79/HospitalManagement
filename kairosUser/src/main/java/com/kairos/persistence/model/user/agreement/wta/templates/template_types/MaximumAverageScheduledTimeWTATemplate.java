package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.List;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE11
 */@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumAverageScheduledTimeWTATemplate extends WTABaseRuleTemplate {

    private List<String> balanceType;//multiple check boxes
    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
    private boolean balanceAdjustment;
    private boolean useShiftTimes;
    private long maximumAvgTime;



    public List<String> getBalanceType() {
        return balanceType;
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


    public boolean isBalanceAdjustment() {
        return balanceAdjustment;
    }

    public void setBalanceAdjustment(boolean balanceAdjustment) {
        this.balanceAdjustment = balanceAdjustment;
    }

    public void setValidationStartDateMillis(long validationStartDateMillis) {
        this.validationStartDateMillis = validationStartDateMillis;
    }

    public boolean isUseShiftTimes() {
        return useShiftTimes;
    }

    public void setUseShiftTimes(boolean useShiftTimes) {
        this.useShiftTimes = useShiftTimes;
    }

    public MaximumAverageScheduledTimeWTATemplate(String name, String templateType, boolean disabled,
                                                  String description, List<String> balanceType, long intervalLength, long validationStartDateMillis
            , boolean balanceAdjustment, boolean useShiftTimes, long maximumAvgTime, String intervalUnit) {
        this.intervalLength = intervalLength;
        this.balanceType = balanceType;

        this.validationStartDateMillis = validationStartDateMillis;
        this.name = name;
        this.templateType = templateType;
        this.disabled = disabled;
        this.description = description;
        this.balanceAdjustment=balanceAdjustment;
        this.useShiftTimes =useShiftTimes;
        this.maximumAvgTime=maximumAvgTime;
        this.intervalUnit=intervalUnit;

    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public long getMaximumAvgTime() {
        return maximumAvgTime;
    }

    public void setMaximumAvgTime(long maximumAvgTime) {
        this.maximumAvgTime = maximumAvgTime;
    }

    public MaximumAverageScheduledTimeWTATemplate() {
    }



}
