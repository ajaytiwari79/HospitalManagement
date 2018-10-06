package com.kairos.dto.activity.counter.chart;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

import com.kairos.dto.activity.counter.enums.ChartType;

public class BaseChart {
    private ChartType type;

    public BaseChart(ChartType type) {
        this.type = type;
    }

    public ChartType getType() {
        return type;
    }

    public void setType(ChartType type) {
        this.type = type;
    }
}
