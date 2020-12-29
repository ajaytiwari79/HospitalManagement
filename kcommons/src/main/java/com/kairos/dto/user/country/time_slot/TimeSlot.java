package com.kairos.dto.user.country.time_slot;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlot {
    private BigInteger id;
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

    public TimeSlot(TimeSlotDTO timeSlotDTO){
        this.id = timeSlotDTO.getId();
        this.name = timeSlotDTO.getName();
        this.startHour = timeSlotDTO.getStartHour();
        this.startMinute = timeSlotDTO.getStartMinute();
        this.endHour = timeSlotDTO.getEndHour();
        this.endMinute = timeSlotDTO.getEndMinute();
        this.shiftStartTime = timeSlotDTO.isShiftStartTime();
    }

    public TimeSlot(BigInteger id,String name, int startHour, int endHour, boolean shiftStartTime) {
        this.id=id;
        this.name = name;
        this.startHour = startHour;
        this.endHour = endHour;
        this.shiftStartTime = shiftStartTime;
    }
}
