package com.kairos.activity.persistence.model.counter;

import com.kairos.activity.persistence.enums.counter.ChartType;
import com.kairos.activity.persistence.enums.counter.CounterSize;
import com.kairos.activity.persistence.enums.counter.CounterType;
import com.kairos.activity.persistence.enums.counter.CounterView;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.util.ArrayList;
import java.util.List;

public class CounterDefinition extends MongoBaseEntity {

    private CounterType type;
    private List<ChartType> chartsSupported = new ArrayList<ChartType>();
    private CounterSize chartSize;
    private CounterView viewSupported;

    public CounterType getType() {
        return type;
    }

    public void setType(CounterType type) {
        this.type = type;
    }

    public List<ChartType> getChartsSupported() {
        return chartsSupported;
    }

    public void setChartsSupported(List<ChartType> chartsSupported) {
        this.chartsSupported = chartsSupported;
    }

    public CounterSize getChartSize() {
        return chartSize;
    }

    public void setChartSize(CounterSize chartSize) {
        this.chartSize = chartSize;
    }

    public CounterView getViewSupported() {
        return viewSupported;
    }

    public void setViewSupported(CounterView viewSupported) {
        this.viewSupported = viewSupported;
    }
}
