package com.kairos.dto.activity.counter.chart;

public class BarLineChartKPiDateUnit extends CommonKpiDataUnit {
    private Double barValue;
    private Double lineValue;

    public BarLineChartKPiDateUnit() {
    }

    public BarLineChartKPiDateUnit(Double barValue, Double lineValue) {
        this.barValue = barValue;
        this.lineValue = lineValue;
    }

    public BarLineChartKPiDateUnit(String label, Number refId, Double barValue,Double lineValue) {
        super(label, refId);
        this.barValue = barValue;
        this.lineValue = lineValue;
    }

    public Double getBarValue() {
        return barValue;
    }

    public void setBarValue(Double barValue) {
        this.barValue = barValue;
    }

    public Double getLineValue() {
        return lineValue;
    }

    public void setLineValue(Double lineValue) {
        this.lineValue = lineValue;
    }
}
