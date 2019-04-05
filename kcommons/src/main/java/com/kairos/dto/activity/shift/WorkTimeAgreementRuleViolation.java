package com.kairos.dto.activity.shift;

import com.kairos.enums.DurationType;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 29/8/18
 */

public class WorkTimeAgreementRuleViolation {

    private BigInteger ruleTemplateId;
    private String name;
    private Integer counter;
    private Integer totalCounter;
    private boolean broken;
    private boolean canBeIgnore;
    private DurationType unitType;
    private String unitValue;


    public WorkTimeAgreementRuleViolation() {
    }

    public WorkTimeAgreementRuleViolation(BigInteger ruleTemplateId, String name, Integer counter, boolean broken, boolean canBeIgnore,Integer totalCounter,DurationType unitType,String unitValue) {
        this.ruleTemplateId = ruleTemplateId;
        this.name = name;
        this.counter = counter;
        this.broken = broken;
        this.canBeIgnore = canBeIgnore;
        this.totalCounter = totalCounter;
        this.unitType = unitType;
        this.unitValue = unitValue;
    }

    public BigInteger getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(BigInteger ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCounter() {
        return counter;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public boolean isCanBeIgnore() {
        return canBeIgnore;
    }

    public void setCanBeIgnore(boolean canBeIgnore) {
        this.canBeIgnore = canBeIgnore;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Integer getTotalCounter() {
        return totalCounter;
    }

    public void setTotalCounter(Integer totalCounter) {
        this.totalCounter = totalCounter;
    }

    public DurationType getUnitType() {
        return unitType;
    }

    public void setUnitType(DurationType unitType) {
        this.unitType = unitType;
    }

    public String getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(String unitValue) {
        this.unitValue = unitValue;
    }
}
