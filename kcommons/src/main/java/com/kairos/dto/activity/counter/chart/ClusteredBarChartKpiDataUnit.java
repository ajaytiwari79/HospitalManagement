package com.kairos.dto.activity.counter.chart;

import java.util.List;

public class ClusteredBarChartKpiDataUnit extends CommonKpiDataUnit {
    private double value;
    private String colorCode;
    private List<ClusteredBarChartKpiDataUnit> subValues;

    public ClusteredBarChartKpiDataUnit() {
    }

    public ClusteredBarChartKpiDataUnit(String label,String colorCode, double value) {
        super(label);
        this.colorCode=colorCode;
        this.value = value;

    }

    public ClusteredBarChartKpiDataUnit(String label,double value, List<ClusteredBarChartKpiDataUnit> subValues) {
        super(label);
        this.value = value;
        this.subValues = subValues;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<ClusteredBarChartKpiDataUnit> getSubValues() {
        return subValues;
    }

    public void setSubValues(List<ClusteredBarChartKpiDataUnit> subValues) {
        this.subValues = subValues;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
