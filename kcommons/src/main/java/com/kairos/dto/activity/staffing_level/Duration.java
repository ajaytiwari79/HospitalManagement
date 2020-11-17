package com.kairos.dto.activity.staffing_level;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kairos.commons.utils.DateTimeInterval;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.kairos.commons.utils.DateUtils.asZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Duration {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime from;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime to;


    public int getDuration() {
        if (Optional.ofNullable(to).isPresent() && Optional.ofNullable(from).isPresent())
            return (int) ChronoUnit.MINUTES.between(from, to);
        else
            return 0;
    }

    public DateTimeInterval getInterval(LocalDate localDate){
        return new DateTimeInterval(asZonedDateTime(localDate,from),from.isAfter(to) ? asZonedDateTime(localDate.plusDays(1),to) : asZonedDateTime(localDate,to));
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
