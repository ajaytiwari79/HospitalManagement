package com.kairos.dto.activity.counter.data;

public class KPIAxisData {
    private String label;
    private String valueField;

    public KPIAxisData(String label, String valueField) {
        this.label = label;
        this.valueField = valueField;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }
}
