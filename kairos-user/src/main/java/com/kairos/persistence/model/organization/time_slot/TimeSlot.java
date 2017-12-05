package com.kairos.persistence.model.organization.time_slot;

import com.kairos.persistence.model.common.UserBaseEntity;
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

    public TimeSlot(String name, int startHour, int startMinute, int endHour, int endMinute,
                    TYPE timeSlotType) {
        this.name = name;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.timeSlotType = timeSlotType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private TYPE timeSlotType;

    private Long kmdExternalId; // for importing time slots from KMD

    public TYPE getTimeSlotType() {
        return timeSlotType;
    }

    public void setTimeSlotType(TYPE timeSlotType) {
        this.timeSlotType = timeSlotType;
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

    public enum TYPE{

        STANDARD,ADVANCE;
    }
}
