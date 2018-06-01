package com.kairos.activity.persistence.model.counter;

import com.kairos.activity.persistence.enums.counter.ChartType;
import com.kairos.activity.persistence.enums.counter.CounterSize;
import com.kairos.activity.persistence.enums.counter.CounterType;
import com.kairos.activity.persistence.enums.counter.CounterView;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.util.ArrayList;
import java.util.List;

public class Counter extends MongoBaseEntity {

    private CounterType type;

    public CounterType getType() {
        return type;
    }

    public void setType(CounterType type) {
        this.type = type;
    }

}
