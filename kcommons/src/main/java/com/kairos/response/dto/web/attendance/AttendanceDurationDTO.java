package com.kairos.response.dto.web.attendance;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceDurationDTO {
    private LocalDate clockInDate;
    private LocalDate clockOutDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime clockInTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime clockOutTime;

    public AttendanceDurationDTO() {
        //Default Constructor
    }

    public AttendanceDurationDTO(LocalDate clockInDate, LocalDate clockOutDate,
                                 LocalTime clockInTime, LocalTime clockOutTime) {
        this.clockInDate = clockInDate;
        this.clockOutDate = clockOutDate;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
    }

    public LocalDate getClockInDate() {
        return clockInDate;
    }

    public void setClockInDate(LocalDate clockInDate) {
        this.clockInDate = clockInDate;
    }

    public LocalDate getClockOutDate() {
        return clockOutDate;
    }

    public void setClockOutDate(LocalDate clockOutDate) {
        this.clockOutDate = clockOutDate;
    }

    public LocalTime getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(LocalTime clockInTime) {
        this.clockInTime = clockInTime;
    }

    public LocalTime getClockOutTime() {
        return clockOutTime;
    }

    public void setClockOutTime(LocalTime clockOutTime) {
        this.clockOutTime = clockOutTime;
    }
}
