package com.kairos.dto.activity.counter.chart;

import com.kairos.dto.activity.counter.enums.ChartType;
import com.kairos.dto.activity.counter.enums.RepresentationUnit;

import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

public class GaugeChart extends BaseChart {
    private double minValue;
    private double maxValue;
    private double value;
    private List<Double> bandInitValues;
    private List<String> bandColors;
    private RepresentationUnit unit;
    private String unitName;

    public GaugeChart(double minValue, double maxValue, double value, List<Double> bandInitValues, List<String> bandColors, RepresentationUnit unit, String unitName) {
        super(ChartType.GAUGE);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = value;
        this.bandInitValues = bandInitValues;
        this.bandColors = bandColors;
        this.unit = unit;
        this.unitName = unitName;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<Double> getBandInitValues() {
        return bandInitValues;
    }

    public void setBandInitValues(List<Double> bandInitValues) {
        this.bandInitValues = bandInitValues;
    }

    public List<String> getBandColors() {
        return bandColors;
    }

    public void setBandColors(List<String> bandColors) {
        this.bandColors = bandColors;
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
