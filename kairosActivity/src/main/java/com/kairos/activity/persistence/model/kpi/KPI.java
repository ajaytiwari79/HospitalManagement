package com.kairos.activity.persistence.model.kpi;

import com.kairos.activity.persistence.enums.counter.CounterType;
import com.kairos.activity.persistence.enums.kpi.KpiType;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;

public class KPI extends MongoBaseEntity {

    private KpiType type;

    public KpiType getType() {
        return type;
    }

    public void setType(KpiType type) {
        this.type = type;
    }

}
