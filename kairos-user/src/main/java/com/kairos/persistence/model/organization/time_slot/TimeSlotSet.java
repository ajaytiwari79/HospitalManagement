package com.kairos.persistence.model.organization.time_slot;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.time_slot.TimeSlotMode;
import com.kairos.response.dto.web.organization.time_slot.TimeSlotSetDTO;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_TIME_SLOT;

/**
 * Created by prabjot on 5/12/17.
 */
@NodeEntity
public class TimeSlotSet extends UserBaseEntity{

    private String name;
    @DateLong
    private Date startDate;
    @DateLong
    private Date endDate;
    @Relationship(type = HAS_TIME_SLOT)
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private TimeSlotMode timeSlotMode;
    private boolean deleted;



    public TimeSlotSet() {
        //default constructor
    }

    public TimeSlotSet(String name, Date startDate) {
        this.name = name;
        this.startDate = startDate;
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

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
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

    public void updateTimeSlotSet(TimeSlotSetDTO timeSlotSetDTO){
        this.endDate = timeSlotSetDTO.getEndDate();
        this.name = timeSlotSetDTO.getName();
    }
}
