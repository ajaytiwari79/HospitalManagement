package com.kairos.persistence.model.organization.time_slot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_TIME_SLOT;

/**
 * Created by prabjot on 8/12/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@RelationshipEntity(type = HAS_TIME_SLOT)
@NoArgsConstructor
@Getter
@Setter
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

}
