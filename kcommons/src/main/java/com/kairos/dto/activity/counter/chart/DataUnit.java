package com.kairos.dto.activity.counter.chart;

public class DataUnit {
    private String label;
    private double value;

    /*
     * @author: mohit.shakya@oodlestechnologies.com
     * @dated: Jun/29/2018
     */

    public DataUnit(String label, double value) {
        this.label = label;
        this.value = value;
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
}
