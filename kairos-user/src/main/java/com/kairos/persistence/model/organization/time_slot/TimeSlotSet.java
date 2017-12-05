package com.kairos.persistence.model.organization.time_slot;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by prabjot on 5/12/17.
 */
@NodeEntity
public class TimeSlotSet extends UserBaseEntity{

    private String name;
    @DateLong
    private LocalDate startDate;
    @DateLong
    private LocalDate endDate;
    private List<TimeSlot> timeSlots;

    public TimeSlotSet() {
        //default constructor
    }

    public TimeSlotSet(String name, LocalDate startDate) {
        this.name = name;
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }
}
