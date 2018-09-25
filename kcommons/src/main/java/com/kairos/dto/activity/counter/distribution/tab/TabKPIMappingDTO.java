package com.kairos.dto.activity.counter.distribution.tab;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.CounterSize;
import com.kairos.dto.activity.counter.enums.KPIValidity;
import com.kairos.dto.activity.counter.enums.LocationType;

import java.math.BigInteger;

public class TabKPIMappingDTO {
    private BigInteger id;
    private String tabId;
    private BigInteger kpiId;
    private CounterSize size;
    private KPIPosition position;
    private ConfLevel level;
    private KPIValidity kpiValidity;
    private LocationType locationType;
    private int priority;

    public TabKPIMappingDTO() {
    }

    public TabKPIMappingDTO(String tabId, BigInteger kpiId) {
        this.tabId = tabId;
        this.kpiId = kpiId;
    }

    public TabKPIMappingDTO(String tabId, BigInteger kpiId, CounterSize size, KPIPosition position) {
        this.tabId = tabId;
        this.kpiId = kpiId;
        this.size = size;
        this.position = position;
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

    public CounterSize getSize() {
        return size;
    }

    public KPIPosition getPosition() {
        return position;
    }

    public void setPosition(KPIPosition position) {
        this.position = position;
    }

    public void setSize(CounterSize size) {
        this.size = size;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public ConfLevel getLevel() {
        return level;
    }

    public void setLevel(ConfLevel level) {
        this.level = level;
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
