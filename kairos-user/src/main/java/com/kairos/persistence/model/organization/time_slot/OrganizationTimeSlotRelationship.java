package com.kairos.persistence.model.organization.time_slot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION_TIME_SLOT;

/**
 * Created by prabjot on 23/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@RelationshipEntity(type = ORGANIZATION_TIME_SLOT)
public class OrganizationTimeSlotRelationship extends UserBaseEntity {

    @StartNode private Organization organization;
    @EndNode private TimeSlot timeSlot;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private boolean isEnabled = true;
    private boolean isShiftStartTime;

    public void setShiftStartTime(boolean shiftStartTime) {
        isShiftStartTime = shiftStartTime;
    }

    public boolean isShiftStartTime() {

        return isShiftStartTime;
    }

    public Organization getOrganization() {
        return organization;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
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

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
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
}
