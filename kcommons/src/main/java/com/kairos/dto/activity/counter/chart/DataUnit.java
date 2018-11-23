package com.kairos.dto.activity.counter.chart;

public class DataUnit {
    private String label;
    private Number refId;
    private double value;

    /*
     * @author: mohit.shakya@oodlestechnologies.com
     * @dated: Jun/29/2018
     */

    public DataUnit(String label, Number refId, double value) {
        this.label = label;
        this.value = value;
        this.refId = refId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Number getRefId() {
        return refId;
    }

    public void setRefId(Number refId) {
        this.refId = refId;
    }
}
