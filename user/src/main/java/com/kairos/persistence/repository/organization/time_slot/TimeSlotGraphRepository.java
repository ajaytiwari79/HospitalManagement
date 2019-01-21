package com.kairos.persistence.repository.organization.time_slot;

import com.kairos.enums.TimeSlotType;
import com.kairos.enums.time_slot.TimeSlotMode;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
import com.kairos.persistence.model.organization.time_slot.TimeSlotSet;
import com.kairos.persistence.model.organization.time_slot.TimeSlotWrapper;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 17/11/16.
 */
public interface TimeSlotGraphRepository extends Neo4jBaseRepository<TimeSlot,Long>{

    @Query("MATCH (org:Organization)-[:HAS_TIME_SLOT_SET]->(timeSlotSet:TimeSlotSet{timeSlotMode:{1}}) where id(org)={0} AND (timeSlotSet.endDate is null OR DATE(timeSlotSet.endDate)>=DATE()) with timeSlotSet order by timeSlotSet.startDate limit 1\n" +
            "MATCH (timeSlotSet)-[r:HAS_TIME_SLOT]->(timeSlot:TimeSlot) with timeSlot order by timeSlot.startHour,r\n" +
            "RETURN id(timeSlot) as id,timeSlot.name as name,r.startHour as startHour,r.startMinute as startMinute,r.endHour as endHour,r.endMinute as endMinute,r.shiftStartTime as shiftStartTime")
    List<TimeSlotWrapper> getTimeSlots(Long unitId, TimeSlotMode timeSlotMode);

    @Query("Match (timeSlotSet:TimeSlotSet),(timeSlot:TimeSlot) where id(timeSlotSet)={0} AND id(timeSlot)={1}\n" +
            "Match (timeSlotSet)-[r:"+HAS_TIME_SLOT+"]->(timeSlot:TimeSlot) set r.name={2},r.startHour={3},r.startMinute={4},r.endHour={5},r.endMinute={6},r.shiftStartTime={7} return {id:id(timeSlot),name:timeSlot.name,startHour:r.startHour,startMinute:r.startMinute,endHour:r.endHour,endMinute:r.endMinute} as timeSlot")
    Map<String,Object> updateTimeSlot(long timeSlotSetId, long timeSlotId, String name, int startHour, int startMinute, int endHour, int endMinute, boolean shiftStartTime);

    @Query("Match (timeSlotSet:TimeSlotSet)-[r:"+HAS_TIME_SLOT+"]->(timeSlot:TimeSlot)\n" +
            "where id(timeSlotSet)={0} and id(timeSlot)={1} set r.deleted=true")
    void deleteTimeSlot(Long timeSlotSetId,Long timeSlotId);

    @Query("Match (org:Organization) where id(org)={0}\n" +
            "Match (org)-[:HAS_TIME_SLOT_SET]->(timeSlotSet:TimeSlotSet{timeSlotMode:org.timeSlotMode}) where (timeSlotSet.endDate is null OR date(timeSlotSet.endDate)>=DATE()) with timeSlotSet order by timeSlotSet.startDate limit 1\n" +
            "Match (timeSlotSet:TimeSlotSet)-[r:HAS_TIME_SLOT]->(timeSlot:TimeSlot) where id(timeSlot)={1} return distinct {id:id(timeSlot),name:timeSlot.name,startHour:r.startHour,startMinute:r.startMinute,endHour:r.endHour,endMinute:r.endMinute} as timeSlot")
    Map<String,Object>  getTimeSlotByUnitIdAndTimeSlotId(long unitId, long timeSlotId);


    @Query("Match (organization:Organization),(timeSlot:TimeSlot) where id(organization)={0} AND timeSlot.kmdExternalId={1}\n" +
            "Match (organization)-[r:"+ORGANIZATION_TIME_SLOT+"]->(timeSlot:TimeSlot)return {id:id(timeSlot),name:timeSlot.name,startHour:r.startHour,startMinute:r.startMinute,endHour:r.endHour,endMinute:r.endMinute} as timeSlot")
    Map<String,Object>  getTimeSlotByUnitIdAndTimeSlotExternalId(long unitId, long kmdExternalId);

    @Query("Match (timeSlotSet:TimeSlotSet)-[r:"+HAS_TIME_SLOT+"{deleted:false}]->(timeSlot:TimeSlot)\n" +
            "where id(timeSlotSet)={0}\n" +
            "return id(timeSlot) as id,timeSlot.name as name,r.startHour as startHour,r.startMinute as startMinute,r.endHour as endHour,r.endMinute as endMinute,r.shiftStartTime as shiftStartTime order by r.startHour")
    List<TimeSlotWrapper> findTimeSlotsByTimeSlotSet(Long timeSlotSetId);

    List<TimeSlot> findBySystemGeneratedTimeSlotsIsTrue();

    @Query("MATCH (org:Organization)-[:"+HAS_TIME_SLOT_SET+"]->(timeSlotSet:TimeSlotSet) where id(org)={0} AND org.timeSlotMode=timeSlotSet.timeSlotMode" +
            " AND timeSlotSet.timeSlotType ={1} with timeSlotSet order by timeSlotSet.startDate limit 1\n" +
            "MATCH (timeSlotSet)-[r:"+HAS_TIME_SLOT+"]->(timeSlot:TimeSlot) with timeSlot order by timeSlot.startHour,r\n" +
            "RETURN id(timeSlot) as id,timeSlot.name as name,r.startHour as startHour,r.startMinute as startMinute,r.endHour as endHour,r.endMinute as endMinute,r.shiftStartTime as shiftStartTime ORDER BY  r.startHour")
    List<TimeSlotWrapper> getShiftPlanningTimeSlotsByUnit(Long unitId,   TimeSlotType timeSlotType);

    @Query("MATCH (org:Organization)-[:"+HAS_TIME_SLOT_SET+"]->(timeSlotSet:TimeSlotSet) where id(org)IN {0} AND org.timeSlotMode=timeSlotSet.timeSlotMode" +
            " AND timeSlotSet.timeSlotType ={1} with timeSlotSet order by timeSlotSet.startDate limit 1\n" +
            "MATCH (timeSlotSet)-[r:"+HAS_TIME_SLOT+"]->(timeSlot:TimeSlot) with timeSlot order by timeSlot.startHour,r\n" +
            "RETURN id(org) as unitId,id(timeSlot) as id,timeSlot.name as name,r.startHour as startHour,r.startMinute as startMinute,r.endHour as endHour,r.endMinute as endMinute,r.shiftStartTime as shiftStartTime ORDER BY  r.startHour")
    List<TimeSlotWrapper> getShiftPlanningTimeSlotsByUnitIds(List<Long> unitId,   TimeSlotType timeSlotType);

    @Query("Match (organization:Organization)-[:"+HAS_TIME_SLOT_SET+"]->(timeSlotSet:TimeSlotSet{timeSlotMode:{1},deleted:false}) where id(organization)={0} AND timeSlotSet.timeSlotType={2} return timeSlotSet order by timeSlotSet.startDate")
    List<TimeSlotSet> findTimeSlotSetsByOrganizationId(Long unitId, TimeSlotMode timeSlotMode,TimeSlotType timeSlotType);

}
