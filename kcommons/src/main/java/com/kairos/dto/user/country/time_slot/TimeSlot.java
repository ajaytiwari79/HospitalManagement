package com.kairos.dto.user.country.time_slot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot {
    private String name;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private boolean shiftStartTime;

    public TimeSlot(int startHour, int endHour){
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public TimeSlot(String name, int startHour, int endHour, boolean shiftStartTime) {
        this.name = name;
        this.startHour = startHour;
        this.endHour = endHour;
        this.shiftStartTime = shiftStartTime;
    }
}
