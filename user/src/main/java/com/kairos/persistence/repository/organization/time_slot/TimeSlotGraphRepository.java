package com.kairos.persistence.repository.organization.time_slot;

import com.kairos.enums.TimeSlotType;
import com.kairos.enums.time_slot.TimeSlotMode;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
import com.kairos.persistence.model.organization.time_slot.TimeSlotSet;
import com.kairos.persistence.model.organization.time_slot.TimeSlotWrapper;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 17/11/16.
 */
public interface TimeSlotGraphRepository extends Neo4jBaseRepository<TimeSlot,Long>{

    @Query("MATCH (org:Unit)-[:HAS_TIME_SLOT_SET]->(timeSlotSet:TimeSlotSet{timeSlotMode:{1}}) where id(org)={0} AND (timeSlotSet.endDate is null OR DATE(timeSlotSet.endDate)>=DATE()) with timeSlotSet order by timeSlotSet.startDate limit 1\n" +
            "MATCH (timeSlotSet)-[r:HAS_TIME_SLOT]->(timeSlot:TimeSlot) with timeSlot order by timeSlot.startHour,r\n" +
            "RETURN id(timeSlot) as id,timeSlot.name as name,r.startHour as startHour,r.startMinute as startMinute,r.endHour as endHour,r.endMinute as endMinute,r.shiftStartTime as shiftStartTime")
    List<TimeSlotWrapper> getTimeSlots(@NotNull Long unitId, @NotNull TimeSlotMode timeSlotMode);


    @Query("MATCH (org)-[:"+HAS_TIME_SLOT_SET+"]->(timeSlotSet:TimeSlotSet) where id(org) IN {0} AND org.timeSlotMode=timeSlotSet.timeSlotMode" +
            " AND timeSlotSet.timeSlotType ={1} with org, timeSlotSet order by timeSlotSet.startDate limit 1\n" +
            "MATCH (timeSlotSet)-[r:"+HAS_TIME_SLOT+"]->(timeSlot:TimeSlot) with  org,r, timeSlot order by timeSlot.startHour \n" +
            "RETURN id(org) as unitId,id(timeSlot) as id,timeSlot.name as name,r.startHour as startHour,r.startMinute as startMinute,r.endHour as endHour,r.endMinute as endMinute,r.shiftStartTime as shiftStartTime ORDER BY  r.startHour")
    List<TimeSlotWrapper> getShiftPlanningTimeSlotsByUnitIds(List<Long> unitId,   TimeSlotType timeSlotType);


}
