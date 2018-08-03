package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class DefaultCounterOrder extends MongoBaseEntity {
    private BigInteger countryId;
    protected String moduleId;
    protected String tabId;
    protected List<BigInteger> orderedCounterIds;

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public BigInteger getCountryId() {
        return countryId;
    }

    public void setCountryId(BigInteger countryId) {
        this.countryId = countryId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public List<BigInteger> getOrderedCounterIds() {
        return orderedCounterIds;
    }

    public void setOrderedCounterIds(List<BigInteger> orderedCounterIds) {
        this.orderedCounterIds = orderedCounterIds;
    }
}
