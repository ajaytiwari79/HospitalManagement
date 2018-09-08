package com.kairos.persistence.model.counter.chart;

import com.kairos.dto.activity.counter.ChartType;
import com.kairos.dto.activity.counter.RepresentationUnit;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

public class SingleNumberChart extends BaseChart {
    private double value;
    private RepresentationUnit unit;
    private String unitName;

    public SingleNumberChart(double value, RepresentationUnit unit, String unitName) {
        super(ChartType.NUMBER_ONLY);
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public RepresentationUnit getUnit() {
        return unit;
    }

    public void setUnit(RepresentationUnit unit) {
        this.unit = unit;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
