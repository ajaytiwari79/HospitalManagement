package com.kairos.dto.activity.counter.data;

import java.math.BigInteger;
import java.util.List;

public class FilterCriteriaDTO {
    private Long countryId;
    private boolean isCountryAdmin;
    private Long unitId;
    private Long staffId;
    private List<FilterCriteria> filters;
    private List<BigInteger> kpiIds;
    private List<BigInteger> counterIds;

    public FilterCriteriaDTO() {
    }

    public FilterCriteriaDTO(List<FilterCriteria> filters, List<BigInteger> kpiIds,Long countryId,boolean isCountryAdmin) {
        this.filters = filters;
        this.kpiIds = kpiIds;
        this.countryId=countryId;
        this.isCountryAdmin=isCountryAdmin;
    }

    public FilterCriteriaDTO(Long unitId, List<BigInteger> kpiIds,Long countryId,boolean isCountryAdmin) {
        this.countryId=countryId;
        this.isCountryAdmin=isCountryAdmin;
        this.unitId = unitId;
        this.kpiIds = kpiIds;
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

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public boolean isCountryAdmin() {
        return isCountryAdmin;
    }

    public void setCountryAdmin(boolean countryAdmin) {
        isCountryAdmin = countryAdmin;
    }

    public Long getCountryId() {

        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

}
