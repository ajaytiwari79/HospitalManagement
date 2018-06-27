package com.kairos.user.agreement.cta.cta_response;

public class CalculateValueAgainstDTO {
    private  String calculateValue;
    private float scale;
    private FixedValueDTO fixedValue;

    public CalculateValueAgainstDTO() {
        //default constractor
    }

    public CalculateValueAgainstDTO(String calculateValue, float scale, FixedValueDTO fixedValue) {
        this.calculateValue = calculateValue;
        this.scale = scale;
        this.fixedValue = fixedValue;
    }

    public String getCalculateValue() {
        return calculateValue;
    }

    public void setCalculateValue(String calculateValue) {
        this.calculateValue = calculateValue;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public FixedValueDTO getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(FixedValueDTO fixedValue) {
        this.fixedValue = fixedValue;
    }


}
