package com.kairos.persistence.model.counter;

import com.kairos.enums.CounterType;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class DefaultCounterSettings extends MongoBaseEntity {
    private BigInteger countryId;
    protected String tabId;
    protected CounterType type;

    public BigInteger getCountryId() {
        return countryId;
    }

    public void setCountryId(BigInteger countryId) {
        this.countryId = countryId;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public CounterType getType() {
        return type;
    }

    public void setType(CounterType type) {
        this.type = type;
    }
}
