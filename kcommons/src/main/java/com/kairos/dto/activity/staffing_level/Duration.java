package com.kairos.dto.activity.staffing_level;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
//import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class Duration {
    //@DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime from;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime to;

    public Duration() {
        //default constructor
    }

    public Duration(LocalTime from, LocalTime to) {
        this.from = from;
        this.to = to;
    }

    public int getDuration() {
        if (Optional.ofNullable(to).isPresent() && Optional.ofNullable(from).isPresent())
            return (int) ChronoUnit.MINUTES.between(from, to);
        else
            return 0;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Duration)) return false;

        Duration that = (Duration) o;

        return new EqualsBuilder()
                .append(from, that.from)
                .append(to, that.to)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(from)
                .append(to)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("from", from)
                .append("to", to)
                .toString();
    }
}
