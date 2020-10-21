package com.kairos.persistence.model.organization.time_slot;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oodles on 14/10/16.
 */
@NodeEntity
@Getter
@Setter
public class TimeSlot extends UserBaseEntity {

    private String name;
    //if value of {systemGeneratedTimeSlots = true},then it will considered
    //as standard time slots
    private boolean systemGeneratedTimeSlots;

    public TimeSlot(String name) {
        this.name = name;
    }
}
