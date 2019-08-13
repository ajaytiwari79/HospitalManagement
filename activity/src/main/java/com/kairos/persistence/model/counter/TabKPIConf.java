package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.distribution.tab.KPIPosition;
import com.kairos.dto.activity.counter.enums.*;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
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
}
