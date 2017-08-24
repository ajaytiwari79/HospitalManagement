package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oodles on 14/10/16.
 */
@NodeEntity
public class TimeSlot extends UserBaseEntity {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private TYPE timeSlotType;

    public TYPE getTimeSlotType() {
        return timeSlotType;
    }

    public void setTimeSlotType(TYPE timeSlotType) {
        this.timeSlotType = timeSlotType;
    }

    public enum TYPE{

        STANDARD,ADVANCE;
    }
}
