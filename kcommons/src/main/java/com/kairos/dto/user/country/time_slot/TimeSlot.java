package com.kairos.dto.user.country.time_slot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TimeSlot {

    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    public TimeSlot(int startHour, int endHour){
        this.startHour = startHour;
        this.endHour = endHour;
    }


}
