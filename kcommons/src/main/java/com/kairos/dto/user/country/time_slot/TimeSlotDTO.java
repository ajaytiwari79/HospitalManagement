package com.kairos.dto.user.country.time_slot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 23/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

    public TimeSlotDTO() {
        //default constructor
    }

    public TimeSlotDTO(String name, int startHour, int startMinute, int endHour, int endMinute) {
        this.name = name;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public boolean isShiftStartTime() {
        return shiftStartTime;
    }

    public void setShiftStartTime(boolean shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {

        return id;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
