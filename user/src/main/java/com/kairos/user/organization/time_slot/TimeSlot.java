package com.kairos.user.organization.time_slot;

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
    private Long kmdExternalId; // for importing time slots from KMD

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


    public Long getKmdExternalId() {
        return kmdExternalId;
    }

    public void setKmdExternalId(Long kmdExternalId) {
        this.kmdExternalId = kmdExternalId;
    }


    public boolean isSystemGeneratedTimeSlots() {
        return systemGeneratedTimeSlots;
    }

    public void setSystemGeneratedTimeSlots(boolean systemGeneratedTimeSlots) {
        this.systemGeneratedTimeSlots = systemGeneratedTimeSlots;
    }


}
