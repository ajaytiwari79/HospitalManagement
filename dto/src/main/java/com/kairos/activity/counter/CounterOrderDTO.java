package com.kairos.activity.counter;

import java.math.BigInteger;
import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class CounterOrderDTO {
    private BigInteger id;
    private String moduleId;
    private String tabId;
    private List<BigInteger> orderedCounterIds;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public List<BigInteger> getOrderedCounterIds() {
        return orderedCounterIds;
    }

    public void setOrderedCounterIds(List<BigInteger> orderedCounterIds) {
        this.orderedCounterIds = orderedCounterIds;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
