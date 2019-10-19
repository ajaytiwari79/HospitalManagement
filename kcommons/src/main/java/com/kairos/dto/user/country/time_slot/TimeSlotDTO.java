package com.kairos.dto.user.country.time_slot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Created by prabjot on 23/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class TimeSlotDTO {
    @NotBlank(message = "error.name.notnull")
    private String name;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private boolean shiftStartTime;
    private Long id;
    private Long unitId;

    public TimeSlotDTO(String name, int startHour, int startMinute, int endHour, int endMinute) {
        this.name = name;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

}
