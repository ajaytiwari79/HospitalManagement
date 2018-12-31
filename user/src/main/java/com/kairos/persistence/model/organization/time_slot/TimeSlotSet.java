package com.kairos.persistence.model.organization.time_slot;

import com.kairos.dto.user.country.time_slot.TimeSlotSetDTO;
import com.kairos.enums.TimeSlotType;
import com.kairos.enums.time_slot.TimeSlotMode;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.neo4j.ogm.annotation.NodeEntity;

import java.time.LocalDate;

/**
 * Created by prabjot on 5/12/17.
 */
@NodeEntity
public class TimeSlotSet extends UserBaseEntity{

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private TimeSlotMode timeSlotMode;
    private TimeSlotType timeSlotType;
    private boolean deleted;
    private boolean defaultSet = false;




    public TimeSlotSet() {
        //default constructor
    }

    public TimeSlotSet(String name, LocalDate startDate,TimeSlotMode timeSlotMode) {
        this.name = name;
        this.startDate = startDate;
        this.timeSlotMode = timeSlotMode;
    }
    public TimeSlotSet(String name, LocalDate startDate,TimeSlotMode timeSlotMode,TimeSlotType timeSlotType) {
        this.name = name;
        this.startDate = startDate;
        this.timeSlotMode = timeSlotMode;
        this.defaultSet=true;
        this.timeSlotType=timeSlotType;
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

    public TimeSlotMode getTimeSlotMode() {
        return timeSlotMode;
    }

    public void setTimeSlotMode(TimeSlotMode timeSlotMode) {
        this.timeSlotMode = timeSlotMode;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public TimeSlotType getTimeSlotType() {
        return timeSlotType;
    }

    public void setTimeSlotType(TimeSlotType timeSlotType) {
        this.timeSlotType = timeSlotType;
    }

    public void updateTimeSlotSet(TimeSlotSetDTO timeSlotSetDTO){
        this.endDate = timeSlotSetDTO.getEndDate();
        this.name = timeSlotSetDTO.getName();
    }

    public boolean isDefaultSet() {
        return defaultSet;
    }

    public void setDefaultSet(boolean defaultSet) {
        this.defaultSet = defaultSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TimeSlotSet that = (TimeSlotSet) o;

        return new EqualsBuilder()
                .append(deleted, that.deleted)
                .append(name, that.name)
                .append(startDate, that.startDate)
                .append(endDate, that.endDate)
                .append(timeSlotMode, that.timeSlotMode)
                .append(id,that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(startDate)
                .append(endDate)
                .append(timeSlotMode)
                .append(deleted)
                .append(id)
                .toHashCode();
    }
}
