package com.kairos.shiftplanning.domain.unit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Created by prabjot on 18/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot {

    private Long id;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private boolean shiftStartTime;
    private String name;

    public TimeSlot(int startHour, int endHour) {
        this.startHour = startHour;
        this.endHour = endHour;
    }
}
