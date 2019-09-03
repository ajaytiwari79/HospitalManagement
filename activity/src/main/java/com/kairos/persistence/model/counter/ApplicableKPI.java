package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.enums.DurationType;
import com.kairos.enums.kpi.KPIRepresentation;
import com.kairos.enums.wta.IntervalUnit;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

public class ApplicableKPI extends MongoBaseEntity {
    private BigInteger activeKpiId;
    private BigInteger baseKpiId;
    private Long countryId;
    private Long unitId;
    private Long staffId;
    private ConfLevel level;
    private String title;
    private ApplicableFilter applicableFilter;
    private List<FibonacciKPIConfig> fibonacciKPIConfigs;
    private KPIRepresentation kpiRepresentation;

    private DurationType frequencyType;
    // frequency value
    private int value;
    private IntervalUnit interval;
    private boolean fibonacciKPI;
    // use for country admin and unit manager if they create copy kpi from bottom instrument of kpi
    private boolean copy;
    public ApplicableKPI() {
        //Default Constructor
    }

    public ApplicableKPI(BigInteger activeKpiId, BigInteger baseKpiId, Long countryId, Long unitId, Long staffId, ConfLevel level) {
        this.activeKpiId = activeKpiId;
        this.baseKpiId = baseKpiId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
    }

    public ApplicableKPI(BigInteger activeKpiId, BigInteger baseKpiId, Long countryId, Long unitId, Long staffId, ConfLevel level, ApplicableFilter applicableFilter,String title,boolean copy,KPIRepresentation kpiRepresentation ,IntervalUnit interval,int value ,DurationType frequencyType,List<FibonacciKPIConfig> fibonacciKPIConfigs) {
        this.activeKpiId = activeKpiId;
        this.baseKpiId = baseKpiId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
        this.applicableFilter=applicableFilter;
        this.copy=copy;
        this.title=title;
        this.kpiRepresentation=kpiRepresentation;
        this.value=value;
        this.interval=interval;
        this.frequencyType=frequencyType;
        this.fibonacciKPIConfigs = fibonacciKPIConfigs;
    }

    public  ApplicableKPI(KPIRepresentation  kpiRepresentation,int value,IntervalUnit interval,DurationType frequencyType){
        this.kpiRepresentation=kpiRepresentation;
        this.value=value;
        this.interval=interval;
        this.frequencyType=frequencyType;
    }

    public ApplicableKPI(BigInteger activeKpiId, BigInteger baseKpiId, Long countryId, Long unitId, Long staffId, ConfLevel level, ApplicableFilter applicableFilter,String title,boolean copy,List<FibonacciKPIConfig> fibonacciKPIConfigs,KPIRepresentation kpiRepresentation) {
        this.activeKpiId = activeKpiId;
        this.baseKpiId = baseKpiId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
        this.applicableFilter=applicableFilter;
        this.copy=copy;
        this.title=title;
        this.fibonacciKPIConfigs = fibonacciKPIConfigs;
        this.kpiRepresentation=kpiRepresentation;
    }

    public BigInteger getActiveKpiId() {
        return activeKpiId;
    }

    public void setActiveKpiId(BigInteger activeKpiId) {
        this.activeKpiId = activeKpiId;
    }

    public ConfLevel getLevel() {
        return level;
    }

    public void setLevel(ConfLevel level) {
        this.level = level;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public BigInteger getBaseKpiId() {
        return baseKpiId;
    }

    public void setBaseKpiId(BigInteger baseKpiId) {
        this.baseKpiId = baseKpiId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public ApplicableFilter getApplicableFilter() {
        return applicableFilter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setApplicableFilter(ApplicableFilter applicableFilter) {
        this.applicableFilter = applicableFilter;
    }

    public boolean isCopy() {
        return copy;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }

    public boolean isFibonacciKPI() {
        return isCollectionNotEmpty(fibonacciKPIConfigs);
    }

    public List<FibonacciKPIConfig> getFibonacciKPIConfigs() {
        return fibonacciKPIConfigs;
    }

    public void setFibonacciKPIConfigs(List<FibonacciKPIConfig> fibonacciKPIConfigs) {
        this.fibonacciKPIConfigs = fibonacciKPIConfigs;
    }

    public KPIRepresentation getKpiRepresentation() {
        return kpiRepresentation;
    }

    public void setKpiRepresentation(KPIRepresentation kpiRepresentation) {
        this.kpiRepresentation = kpiRepresentation;
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

    public IntervalUnit getInterval() {
        return interval;
    }

    public void setInterval(IntervalUnit interval) {
        this.interval = interval;
    }

    private enum GraphType {
        DATA_PER_TIMESLOT,STACKED,COLUMN


    }
}
