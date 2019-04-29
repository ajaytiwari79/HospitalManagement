package com.kairos.dto.activity.counter.data;

import com.kairos.enums.DurationType;
import com.kairos.enums.kpi.Interval;
import com.kairos.enums.kpi.KPIRepresentation;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public class FilterCriteriaDTO {
    private Long countryId;
    private boolean isCountryAdmin;
    private boolean management;
    private Long unitId;
    private Long staffId;
    private List<FilterCriteria> filters;
    private List<BigInteger> kpiIds;
    private List<BigInteger> counterIds;
    private DurationType frequencyType;
    // frequency value
    private int value;
    private KPIRepresentation kpiRepresentation;
    private Interval interval;
    private LocalDate startDate;
    private LocalDate endDate;
    public FilterCriteriaDTO() {
    }

    public FilterCriteriaDTO(List<FilterCriteria> filters, List<BigInteger> kpiIds,Long countryId,boolean isCountryAdmin,KPIRepresentation kpiRepresentation,Interval interval,int value,DurationType frequencyType) {
        this.filters = filters;
        this.kpiIds = kpiIds;
        this.countryId=countryId;
        this.isCountryAdmin=isCountryAdmin;
        this.value=value;
        this.frequencyType=frequencyType;
        this.kpiRepresentation=kpiRepresentation;
        this.interval=interval;
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

    public boolean isManagement() {
        return management;
    }

    public void setManagement(boolean management) {
        this.management = management;
    }

    public DurationType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(DurationType frequencyType) {
        this.frequencyType = frequencyType;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public KPIRepresentation getKpiRepresentation() {
        return kpiRepresentation;
    }

    public void setKpiRepresentation(KPIRepresentation kpiRepresentation) {
        this.kpiRepresentation = kpiRepresentation;
    }
}
