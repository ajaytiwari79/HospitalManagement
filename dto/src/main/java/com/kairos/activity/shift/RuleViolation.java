package com.kairos.activity.shift;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 29/8/18
 */

public class RuleViolation {

    BigInteger id;
    String name;
    int counter;
    boolean broken;
    boolean canBeIgnore;


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
