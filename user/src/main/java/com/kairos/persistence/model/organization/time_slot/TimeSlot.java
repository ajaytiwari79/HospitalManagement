package com.kairos.persistence.model.organization.time_slot;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oodles on 14/10/16.
 */
@NodeEntity
public class TimeSlot extends UserBaseEntity {

    private String name;
    //if value of {systemGeneratedTimeSlots = true},then it will considered
    //as standard time slots
    private boolean systemGeneratedTimeSlots;

    public TimeSlot() {
        //default constructor
    }

    public TimeSlot(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSystemGeneratedTimeSlots() {
        return systemGeneratedTimeSlots;
    }

    public void setSystemGeneratedTimeSlots(boolean systemGeneratedTimeSlots) {
        this.systemGeneratedTimeSlots = systemGeneratedTimeSlots;
    }


}
