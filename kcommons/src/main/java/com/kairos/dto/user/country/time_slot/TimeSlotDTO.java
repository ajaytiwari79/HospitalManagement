package com.kairos.dto.user.country.time_slot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

/**
 * Created by prabjot on 23/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class TimeSlotDTO implements Serializable {
    @NotBlank(message = "error.name.notnull")
    private String name;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private boolean shiftStartTime;
    private BigInteger id;
    private Long unitId;
    private static final long serialVersionUID = 213213213213l;

    public TimeSlotDTO(String name, int startHour, int startMinute, int endHour, int endMinute) {
        this.name = name;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public ZonedDateTime getStartZoneDateTime(LocalDate localDate) {
        return DateUtils.asZonedDateTime(localDate, LocalTime.of(startHour,startMinute));
    }

    public ZonedDateTime getEndZoneDateTime(LocalDate localDate) {
        if(endHour<startHour){
            localDate = localDate.plusDays(1);
        }
        return DateUtils.asZonedDateTime(localDate, LocalTime.of(endHour,endMinute));
    }
}
