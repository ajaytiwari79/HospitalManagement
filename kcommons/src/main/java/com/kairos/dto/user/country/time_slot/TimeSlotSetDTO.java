package com.kairos.dto.user.country.time_slot;

import com.kairos.enums.TimeSlotType;
import com.kairos.enums.time_slot.TimeSlotMode;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;
import java.util.List;

/**
 * Created by prabjot on 5/12/17.
 */
public class TimeSlotSetDTO {

    @NotEmpty(message = "Time slot set name can't be empty")
    private String name;
    private Date startDate;
    private Date endDate;
    private List<TimeSlotDTO> timeSlots;
    private TimeSlotMode timeSlotMode;
    private TimeSlotType timeSlotType;

    public TimeSlotSetDTO() {
        //default constructor
    }

    public TimeSlotSetDTO(String name, Date startDate, Date endDate, List<TimeSlotDTO> timeSlots, TimeSlotMode timeSlotMode) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeSlots = timeSlots;
        this.timeSlotMode = timeSlotMode;
    }

    public TimeSlotSetDTO(String name,Date endDate) {
        this.name = name;
        this.endDate = endDate;
    }

    public TimeSlotSetDTO(Date endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<TimeSlotDTO> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlotDTO> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public TimeSlotMode getTimeSlotMode() {
        return timeSlotMode;
    }

    public void setTimeSlotMode(TimeSlotMode timeSlotMode) {
        this.timeSlotMode = timeSlotMode;
    }

    public TimeSlotType getTimeSlotType() {
        return timeSlotType;
    }

    public void setTimeSlotType(TimeSlotType timeSlotType) {
        this.timeSlotType = timeSlotType;
    }
}
