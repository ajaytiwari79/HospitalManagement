package com.kairos.dto.activity.counter.data;

import java.math.BigInteger;
import java.util.List;

public class FilterCriteriaDTO {
    private Long unitId;
    private List<FilterCriteria> filters;
    private List<BigInteger> kpiIds;
    private List<BigInteger> counterIds;

    public FilterCriteriaDTO() {
    }

    public FilterCriteriaDTO(List<FilterCriteria> filters) {
        this.filters = filters;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<FilterCriteria> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterCriteria> filters) {
        this.filters = filters;
    }

    public List<BigInteger> getKpiIds() {
        return kpiIds;
    }

    public void setKpiIds(List<BigInteger> kpiIds) {
        this.kpiIds = kpiIds;
    }

    public List<BigInteger> getCounterIds() {
        return counterIds;
    }

    public void setCounterIds(List<BigInteger> counterIds) {
        this.counterIds = counterIds;
    }
}
