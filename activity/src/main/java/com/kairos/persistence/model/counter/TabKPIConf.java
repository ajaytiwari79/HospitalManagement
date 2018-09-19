package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.distribution.tab.KPIPosition;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.CounterSize;
import com.kairos.dto.activity.counter.enums.KPIValidity;
import com.kairos.dto.activity.counter.enums.LocationType;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class TabKPIConf extends MongoBaseEntity {
    private String tabId;
    private BigInteger kpiId;
    private Long countryId;
    private Long unitId;
    private Long staffId;
    private ConfLevel level;
    private KPIValidity kpiValidity;
    private LocationType locationType;
    private CounterSize size;
    private KPIPosition position;
    private int priority;

    public TabKPIConf() {

    }

    public TabKPIConf(String tabId, BigInteger kpiId, Long countryId, Long unitId, Long staffId, ConfLevel level,KPIPosition position,KPIValidity kpiValidity,LocationType locationType,int priority) {
        this.tabId = tabId;
        this.kpiId = kpiId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
        this.position = position;
        this.kpiValidity=kpiValidity;
        this.locationType=locationType;
        this.priority=priority;
    }


    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }


    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
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

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public ConfLevel getLevel() {
        return level;
    }

    public void setLevel(ConfLevel level) {
        this.level = level;
    }

    public CounterSize getSize() {
        return size;
    }

    public void setSize(CounterSize size) {
        this.size = size;
    }

    public KPIPosition getPosition() {
        return position;
    }

    public void setPosition(KPIPosition position) {
        this.position = position;
    }

    public KPIValidity getKpiValidity() {
        return kpiValidity;
    }

    public void setKpiValidity(KPIValidity kpiValidity) {
        this.kpiValidity = kpiValidity;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
