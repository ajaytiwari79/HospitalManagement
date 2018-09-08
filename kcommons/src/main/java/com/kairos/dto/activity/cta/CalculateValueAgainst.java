package com.kairos.dto.activity.cta;

public class CalculateValueAgainst{
    private CalculateValueType calculateValue;
    private float scale;
    private FixedValue fixedValue;

    public CalculateValueAgainst() {
        //default constractor
    }

    public CalculateValueAgainst(CalculateValueType calculateValueType, float scale, FixedValue fixedValue) {
        this.calculateValue = calculateValueType;
        this.scale = scale;
        this.fixedValue = fixedValue;
    }

    public CalculateValueType getCalculateValue() {
        return calculateValue;
    }

    public void setCalculateValue(CalculateValueType calculateValueType) {
        this.calculateValue = calculateValueType;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public FixedValue getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(FixedValue fixedValue) {
        this.fixedValue = fixedValue;
    }



}
