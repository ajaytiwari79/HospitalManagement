package com.kairos.persistence.model.user.agreement.cta;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.time.LocalDate;

@NodeEntity
public class CompensationTableInterval extends UserBaseEntity {
   // private LocalTime from;
   // private LocalTime to;
    private float value;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @DateLong
     private LocalDate startDate;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

/*public CompensationTableInterval(LocalDate startDate) {
        this.startDate = startDate;
    }*/

    public CompensationTableInterval() {
        //default constructor
    }

    /*public CompensationTableInterval(LocalTime from, LocalTime to, float value) {
        this.from = from;
        this.to = to;
        this.value = value;
    }*/

    /*public LocalTime getFrom() {
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
    }*/
}
