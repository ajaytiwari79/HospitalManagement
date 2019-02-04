package com.kairos.persistence.model.organization.time_slot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_TIME_SLOT;

/**
 * Created by prabjot on 8/12/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@RelationshipEntity(type = HAS_TIME_SLOT)
public class TimeSlotSetTimeSlotRelationship extends UserBaseEntity {

    @StartNode
    private TimeSlotSet timeSlotSet;
    @EndNode
    private TimeSlot timeSlot;

    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private boolean shiftStartTime;

    public TimeSlotSetTimeSlotRelationship() {
        //default constructor
    }

    public TimeSlotSetTimeSlotRelationship(TimeSlotSet timeSlotSet, TimeSlot timeSlot, int startHour, int startMinute, int endHour, int endMinute) {
        this.timeSlotSet = timeSlotSet;
        this.timeSlot = timeSlot;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public TimeSlotSet getTimeSlotSet() {
        return timeSlotSet;
    }

    public void setTimeSlotSet(TimeSlotSet timeSlotSet) {
        this.timeSlotSet = timeSlotSet;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
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
}
