package com.kairos.dto.activity.counter.chart;

public class CommonKpiDataUnit {
    protected String label;
    protected Number refId;

    /*
     * @author: mohit.shakya@oodlestechnologies.com
     * @dated: Jun/29/2018
     */

    public CommonKpiDataUnit() {
    }

    public CommonKpiDataUnit(String label, Number refId) {
        this.label = label;
        this.refId = refId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public Number getRefId() {
        return refId;
    }

    public void setRefId(Number refId) {
        this.refId = refId;
    }

}
