package com.kairos.dto.activity.counter.data;

public class KPIAxisData {
    private String lablel;
    private String valueField;

    public KPIAxisData(String lablel, String valueField) {
        this.lablel = lablel;
        this.valueField = valueField;
    }

    public String getLablel() {
        return lablel;
    }

    public void setLablel(String lablel) {
        this.lablel = lablel;
    }

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }
}
