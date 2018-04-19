package com.planning.responseDto.PlanningDto.shiftPlanningDto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import java.time.LocalTime;

public class StaffingLevelDuration {

    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime from;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime to;

    public StaffingLevelDuration() {
        //default constructor
    }

    public StaffingLevelDuration(LocalTime from, LocalTime to) {
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
