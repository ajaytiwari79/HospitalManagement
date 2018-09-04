package com.kairos.activity.counter.data;

import java.math.BigInteger;
import java.util.List;

public class FilterCriteriaDTO {
    private Long currentCountryId;
    private Long currentUnitId;
    private List<FilterCriteria> filters;
    private List<BigInteger> counterIds;

    public FilterCriteriaDTO() {
    }

    public FilterCriteriaDTO(List<FilterCriteria> filters) {
        this.filters = filters;
    }

    public Long getCurrentCountryId() {
        return currentCountryId;
    }

    public void setCurrentCountryId(Long currentCountryId) {
        this.currentCountryId = currentCountryId;
    }

    public Long getCurrentUnitId() {
        return currentUnitId;
    }

    public void setCurrentUnitId(Long currentUnitId) {
        this.currentUnitId = currentUnitId;
    }

    public List<FilterCriteria> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterCriteria> filters) {
        this.filters = filters;
    }

    public List<BigInteger> getCounterIds() {
        return counterIds;
    }

    public void setCounterIds(List<BigInteger> counterIds) {
        this.counterIds = counterIds;
    }
}
