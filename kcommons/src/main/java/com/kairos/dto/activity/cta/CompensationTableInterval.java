package com.kairos.dto.activity.cta;

import com.kairos.dto.user.country.agreement.cta.CompensationMeasurementType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalTime;
import java.util.Objects;


public class CompensationTableInterval {
    private LocalTime from;
    private LocalTime to;
    private float value;
    private CompensationMeasurementType compensationMeasurementType;


    public LocalTime getFrom() {
        return from;
    }

    public void setFrom(LocalTime from) {
        this.from = from;
    }

    public LocalTime getTo() {
        return to;
    }

    public void setTo(LocalTime to) {
        this.to = to;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public CompensationMeasurementType getCompensationMeasurementType() {
        return compensationMeasurementType;
    }

    public void setCompensationMeasurementType(CompensationMeasurementType compensationMeasurementType) {
        this.compensationMeasurementType = compensationMeasurementType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompensationTableInterval)) return false;
        CompensationTableInterval that = (CompensationTableInterval) o;
        return Float.compare(that.value, value) == 0 &&
                Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) &&
                compensationMeasurementType == that.compensationMeasurementType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, value, compensationMeasurementType);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("from", from)
                .append("to", to)
                .append("value", value)
                .toString();
    }
}
