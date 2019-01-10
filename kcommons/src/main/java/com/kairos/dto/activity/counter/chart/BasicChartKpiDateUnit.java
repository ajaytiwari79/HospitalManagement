package com.kairos.dto.activity.counter.chart;

public class BasicChartKpiDateUnit extends CommonKpiDataUnit {
    private double value;

    public BasicChartKpiDateUnit() {
    }

    public BasicChartKpiDateUnit(double value) {
        this.value = value;
    }

    public BasicChartKpiDateUnit(String label, Number refId, double value) {
        super(label, refId);
        this.value = value;
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
    }
    @Override
    public void setRefId(Number refId) {
        this.refId = refId;
    }



    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

}
