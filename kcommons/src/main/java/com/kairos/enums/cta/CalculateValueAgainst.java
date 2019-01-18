package com.kairos.enums.cta;

import com.kairos.dto.activity.cta.FixedValue;

import java.util.Objects;

public class CalculateValueAgainst{
    private CalculateValueType calculateValue;
    private float scale;
    private FixedValue fixedValue;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalculateValueAgainst)) return false;
        CalculateValueAgainst that = (CalculateValueAgainst) o;
        return Float.compare(that.scale, scale) == 0 &&
                calculateValue == that.calculateValue &&
                Objects.equals(fixedValue, that.fixedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calculateValue, scale, fixedValue);
    }
}
