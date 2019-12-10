package com.kairos.dto.activity.counter.distribution.tab;

import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.CounterSize;
import com.kairos.dto.activity.counter.enums.KPIValidity;
import com.kairos.dto.activity.counter.enums.LocationType;

import java.math.BigInteger;

public class TabKPIDTO {
    private BigInteger id;
    private String tabId;
    private KPIDTO kpi;
    private BigInteger kpiId;
    private CommonRepresentationData data;
    private KPIPosition position;
    private CounterSize size;
    private ConfLevel level;
    private KPIValidity kpiValidity;
    private LocationType locationType;
    private int priority;

    public TabKPIDTO() {
    }


    public TabKPIDTO(String tabId, KPIDTO kpi,CounterSize size) {
        this.tabId = tabId;
        this.kpi = kpi;
        this.size=size;
    }


    public String getTabId() {
        return tabId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public KPIDTO getKpi() {
        return kpi;
    }

    public void setKpi(KPIDTO kpi) {
        this.kpi = kpi;
    }

    public KPIPosition getPosition() {
        return position;
    }

    public void setPosition(KPIPosition position) {
        this.position = position;
    }

    public CounterSize getSize() {
        return size;
    }

    public void setSize(CounterSize size) {
        this.size = size;
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

    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
    }

    public CommonRepresentationData getData() {
        return data;
    }

    public void setData(CommonRepresentationData data) {
        this.data = data;
    }
}
