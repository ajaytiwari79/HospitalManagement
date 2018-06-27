package com.kairos.persistence.model.agreement.cta;

import com.kairos.config.neo4j.converter.LocalTimeStringConverter;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.time.LocalTime;

@NodeEntity
public class CompensationTableInterval extends UserBaseEntity {
   @Convert(LocalTimeStringConverter.class)
    private LocalTime from;
    @Convert(LocalTimeStringConverter.class)
    private LocalTime to;
    private float value;
    private CompensationMeasurementType compensationMeasurementType;

    public CompensationTableInterval() {
        //default constructor
    }

    public CompensationTableInterval(LocalTime from, LocalTime to, float value, CompensationMeasurementType compensationMeasurementType) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.setCompensationMeasurementType(CompensationMeasurementType.MINUTES);
    }

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

        return new EqualsBuilder()
                .append(value, that.value)
                .append(from, that.from)
                .append(to, that.to)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(from)
                .append(to)
                .append(value)
                .toHashCode();
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
