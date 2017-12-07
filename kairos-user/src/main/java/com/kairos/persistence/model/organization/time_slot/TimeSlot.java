package com.kairos.persistence.model.organization.time_slot;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.time_slot.TimeSlotMode;
import com.kairos.response.dto.web.organization.time_slot.TimeSlotDTO;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oodles on 14/10/16.
 */
@NodeEntity
public class TimeSlot extends UserBaseEntity {

    private String name;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private boolean isShiftStartTime;

    public TimeSlot() {
        //default constructor
    }

    public TimeSlot(String name, int startHour, int endHour, TimeSlotMode timeSlotTimeSlotMode) {
        this.name = name;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public TimeSlot(String name, int startHour, int startMinute, int endHour, int endMinute) {
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

    private TimeSlotMode timeSlotMode;

    private Long kmdExternalId; // for importing time slots from KMD

    public TimeSlotMode getTimeSlotMode() {
        return timeSlotMode;
    }

    public void setTimeSlotMode(TimeSlotMode timeSlotMode) {
        this.timeSlotMode = timeSlotMode;
    }

    public Long getKmdExternalId() {
        return kmdExternalId;
    }

    public void setKmdExternalId(Long kmdExternalId) {
        this.kmdExternalId = kmdExternalId;
    }

    public boolean isShiftStartTime() {
        return isShiftStartTime;
    }

    public void setShiftStartTime(boolean shiftStartTime) {
        isShiftStartTime = shiftStartTime;
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

    public TimeSlot updateTimeSlot(TimeSlotDTO timeSlotDTO){
        this.startHour = timeSlotDTO.getStartHour();
        this.startMinute = timeSlotDTO.getStartMinute();
        this.endHour = timeSlotDTO.getEndHour();
        this.endMinute = timeSlotDTO.getEndMinute();
        this.isShiftStartTime = timeSlotDTO.isShiftStartTime();
        return this;
    }


}
