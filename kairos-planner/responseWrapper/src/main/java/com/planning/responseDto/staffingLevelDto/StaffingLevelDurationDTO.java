package com.planning.responseDto.staffingLevelDto;


import java.time.LocalTime;

public class StaffingLevelDurationDTO {
    private LocalTime from;
    private LocalTime to;

    public StaffingLevelDurationDTO() {
        //default constructor
    }

    public StaffingLevelDurationDTO(LocalTime from, LocalTime to) {
        this.from = from;
        this.to = to;
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

}
