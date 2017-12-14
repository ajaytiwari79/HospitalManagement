package com.kairos.persistence.repository.organization.time_slot;

import com.kairos.persistence.model.enums.time_slot.TimeSlotMode;
import com.kairos.persistence.model.organization.time_slot.TimeSlot;
import com.kairos.persistence.model.organization.time_slot.TimeSlotSet;
import com.kairos.persistence.model.organization.time_slot.TimeSlotWrapper;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 17/11/16.
 */
public interface TimeSlotGraphRepository extends GraphRepository<TimeSlot>{

    @Query("Match (org:Organization)-[:HAS_TIME_SLOT_SET]->(timeSlotSet:TimeSlotSet{timeSlotMode:{1}}) where id(org)={0} AND (timeSlotSet.endDate=null OR timeSlotSet.endDate>={2})\n" +
            "Match (timeSlotSet)-[r:HAS_TIME_SLOT]->(timeSlot:TimeSlot)\n" +
            "return id(timeSlot) as id,timeSlot.name as name,r.startHour as startHour,r.startMinute as startMinute,r.endHour as endHour,r.endMinute as endMinute,r.isShiftStartTime as shiftStartTime order by timeSlotSet.startDate DESC LIMIT 1")
    List<TimeSlotWrapper> getTimeSlots(Long unitId, TimeSlotMode timeSlotMode, Date currentDate);


    @Query("Match (timeSlotSet:TimeSlotSet),(timeSlot:TimeSlot) where id(timeSlotSet)={0} AND id(timeSlot)={1}\n" +
            "Match (timeSlotSet)-[r:"+HAS_TIME_SLOT+"]->(timeSlot:TimeSlot) set r.name={2},r.startHour={3},r.startMinute={4},r.endHour={5},r.endMinute={6},r.isShiftStartTime={7} return {id:id(timeSlot),name:timeSlot.name,startHour:r.startHour,startMinute:r.startMinute,endHour:r.endHour,endMinute:r.endMinute} as timeSlot")
    Map<String,Object> updateTimeSlot(long timeSlotSetId, long timeSlotId, String name, int startHour, int startMinute, int endHour, int endMinute, boolean isShiftStartTime);

    @Query("Match (timeSlotSet:TimeSlotSet)-[r:"+HAS_TIME_SLOT+"]->(timeSlot:TimeSlot)\n" +
            "where id(timeSlotSet)={0} and id(timeSlot)={1} set r.deleted=true")
    void deleteTimeSlot(Long timeSlotSetId,Long timeSlotId);

    @Query("Match (organization:Organization),(timeSlot:TimeSlot) where id(organization)={0} AND id(timeSlot)={1}\n" +
            "Match (organization)-[r:ORGANIZATION_TIME_SLOT]->(timeSlot:TimeSlot)return {id:id(timeSlot),name:timeSlot.name,startHour:r.startHour,startMinute:r.startMinute,endHour:r.endHour,endMinute:r.endMinute} as timeSlot")
    Map<String,Object>  getTimeSlotByUnitIdAndTimeSlotId(long unitId, long timeSlotId);

    @Query("Match (n:Organization)-[r:"+ORGANIZATION_TIME_SLOT+"]->(timeSlot:TimeSlot{timeSlotType:{1}}) where id(n)={0} set r.isShiftStartTime=false return r")
    void updateShiftStartTime(long unitId, TimeSlotMode timeSlotMode);

    @Query("Match (organization:Organization),(timeSlot:TimeSlot) where id(organization)={0} AND timeSlot.kmdExternalId={1}\n" +
            "Match (organization)-[r:"+ORGANIZATION_TIME_SLOT+"]->(timeSlot:TimeSlot)return {id:id(timeSlot),name:timeSlot.name,startHour:r.startHour,startMinute:r.startMinute,endHour:r.endHour,endMinute:r.endMinute} as timeSlot")
    Map<String,Object>  getTimeSlotByUnitIdAndTimeSlotExternalId(long unitId, long kmdExternalId);

    @Query("Match (organization:Organization) where id(organization)={0} with organization,{timeSlotType:case when organization.standardTimeSlot then 'STANDARD' else 'ADVANCE' end} as type \n"+
            "Match (organization)-[r:ORGANIZATION_TIME_SLOT{isEnabled:true}]->(timeSlot:TimeSlot{timeSlotType:type.timeSlotType})\n"+
            "return {id:id(timeSlot),name:timeSlot.name,startHour:r.startHour,startMinute:r.startMinute,endHour:r.endHour,endMinute:r.endMinute,isShiftStartTime:r.isShiftStartTime} as timeSlot order by r.startHour")
    List<Map<String,Object>> getUnitCurrentTimeSlots(long unitId);

    TimeSlot findByKmdExternalId(Long kmdExternalId);

    @Query("Match (organization:Organization),(timeSlot:TimeSlot) where id(organization)={0} AND timeSlot.kmdExternalId={1}\n" +
            "Match (organization)-[r:ORGANIZATION_TIME_SLOT]->(timeSlot) return count(r)>0")
    Boolean hasTimeSlotExistByUnitIdAndTimeSlotId(long unitId, long kmdExternalId);

    @Query("Match (organization:Organization),(timeSlot:TimeSlot) where id(organization)={0} AND timeSlot.kmdExternalId={1}\n" +
            "Match (organization)-[r:ORGANIZATION_TIME_SLOT]->(timeSlot) delete r")
    void removeTimeSlotExistByUnitIdAndTimeSlotId(long unitId, long kmdExternalId);

    @Query("Match (organization:Organization)-[:"+HAS_TIME_SLOT_SET+"]->(timeSlotSet:TimeSlotSet{timeSlotMode:{1},deleted:false}) where id(organization)={0} return timeSlotSet order by timeSlotSet.startDate")
    List<TimeSlotSet> findTimeSlotsByOrganizationId(Long unitId, TimeSlotMode timeSlotMode);

    @Query("Match (timeSlotSet:TimeSlotSet)-[r:"+HAS_TIME_SLOT+"{deleted:false}]->(timeSlot:TimeSlot)\n" +
            "where id(timeSlotSet)={0}\n" +
            "return id(timeSlot) as id,timeSlot.name as name,r.startHour as startHour,r.startMinute as startMinute,r.endHour as endHour,r.endMinute as endMinute,r.isShiftStartTime as shiftStartTime order by r.startHour")
    List<TimeSlotWrapper> findTimeSlotsByTimeSlotSet(Long timeSlotSetId);

    List<TimeSlot> findBySystemGeneratedTimeSlotsIsTrue();




}
