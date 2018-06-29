package com.kairos.activity.persistence.model.counter.chart;

public class PieDataUnit {
    private String label;
    private String value;

    /*
     * @author: mohit.shakya@oodlestechnologies.com
     * @dated: Jun/29/2018
     */

    public PieDataUnit(String label, String value){
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
