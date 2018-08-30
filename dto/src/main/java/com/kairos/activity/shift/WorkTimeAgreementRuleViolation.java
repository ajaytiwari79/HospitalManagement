package com.kairos.activity.shift;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 29/8/18
 */

public class WorkTimeAgreementRuleViolation {

    private BigInteger id;
    private String name;
    private int counter;
    private boolean broken;
    private boolean canBeIgnore;


    public WorkTimeAgreementRuleViolation() {
    }

    public WorkTimeAgreementRuleViolation(BigInteger id, String name, int counter, boolean broken, boolean canBeIgnore) {
        this.id = id;
        this.name = name;
        this.counter = counter;
        this.broken = broken;
        this.canBeIgnore = canBeIgnore;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
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
}
