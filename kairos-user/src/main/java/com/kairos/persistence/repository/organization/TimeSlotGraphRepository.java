package com.kairos.persistence.repository.organization;
import com.kairos.persistence.model.organization.TimeSlot;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION_TIME_SLOT;


/**
 * Created by oodles on 17/11/16.
 */
public interface TimeSlotGraphRepository extends GraphRepository<TimeSlot>{

    List<TimeSlot> findByTimeSlotType(TimeSlot.TYPE type);

    @Query("Match (organization:Organization) where id(organization)={0}\n" +
            "Match (organization)-[r:"+ORGANIZATION_TIME_SLOT+"{isEnabled:true}]->(timeSlot:TimeSlot{timeSlotType:{1}}) return {id:id(timeSlot),name:timeSlot.name,startHour:r.startHour,startMinute:r.startMinute,endHour:r.endHour,endMinute:r.endMinute,isShiftStartTime:r.isShiftStartTime} as timeSlot order by r.startHour")
    List<Map<String,Object>> getTimeSlots(long unitId, TimeSlot.TYPE type);

    @Query("Match (organization:Organization),(timeSlot:TimeSlot) where id(organization)={0} AND id(timeSlot)={1}\n" +
            "Match (organization)-[r:"+ORGANIZATION_TIME_SLOT+"]->(timeSlot:TimeSlot) set r.startHour={2},r.startMinute={3},r.endHour={4},r.endMinute={5},r.isShiftStartTime={6} return {id:id(timeSlot),name:timeSlot.name,startHour:r.startHour,startMinute:r.startMinute,endHour:r.endHour,endMinute:r.endMinute} as timeSlot")
    Map<String,Object> updateStandardTimeSlot(long unitId, long timeSlotId, int startHour, int startMinute, int endHour, int endMinute, boolean isShiftStartTime);

    @Query("Match (organization:Organization),(timeSlot:TimeSlot) where id(organization)={0} AND id(timeSlot)={1}\n" +
            "Match (organization)-[r:"+ORGANIZATION_TIME_SLOT+"]->(timeSlot:TimeSlot) set r.name={2},r.startHour={3},r.startMinute={4},r.endHour={5},r.endMinute={6},r.isShiftStartTime={6} return {id:id(timeSlot),name:timeSlot.name,startHour:r.startHour,startMinute:r.startMinute,endHour:r.endHour,endMinute:r.endMinute} as timeSlot")
    Map<String,Object> updateAdvanceTimeSlot(long unitId, long timeSlotId, String name, int startHour, int startMinute, int endHour, int endMinute, boolean isShiftStartTime);

    @Query("Match (organization:Organization),(timeSlot:TimeSlot) where id(organization)={0} AND id(timeSlot)={1}\n" +
            "Match (organization)-[r:"+ORGANIZATION_TIME_SLOT+"]->(timeSlot:TimeSlot) set r.isEnabled=false return r is null")
    boolean deleteTimeSlot(long unitId, long timeSlotId);

    @Query("Match (organization:Organization),(timeSlot:TimeSlot) where id(organization)={0} AND id(timeSlot)={1}\n" +
            "Match (organization)-[r:ORGANIZATION_TIME_SLOT]->(timeSlot:TimeSlot)return {id:id(timeSlot),name:timeSlot.name,startHour:r.startHour,startMinute:r.startMinute,endHour:r.endHour,endMinute:r.endMinute} as timeSlot")
    Map<String,Object>  getTimeSlotByUnitIdAndTimeSlotId(long unitId, long timeSlotId);

    @Query("Match (n:Organization)-[r:"+ORGANIZATION_TIME_SLOT+"]->(timeSlot:TimeSlot{timeSlotType:{1}}) where id(n)={0} set r.isShiftStartTime=false return r")
    void updateShiftStartTime(long unitId, TimeSlot.TYPE type);

    @Query("Match (organization:Organization),(timeSlot:TimeSlot) where id(organization)={0} AND timeSlot.name={1}\n" +
            "Match (organization)-[r:ORGANIZATION_TIME_SLOT]->(timeSlot:TimeSlot)return {id:id(timeSlot),name:timeSlot.name,startHour:r.startHour,startMinute:r.startMinute,endHour:r.endHour,endMinute:r.endMinute} as timeSlot")
    Map<String,Object>  getTimeSlotByUnitIdAndTimeSlotName(long unitId, String timeSlotName);

    @Query("Match (organization:Organization) where id(organization)={0} with organization,{timeSlotType:case when organization.standardTimeSlot then 'STANDARD' else 'ADVANCE' end} as type \n"+
            "Match (organization)-[r:ORGANIZATION_TIME_SLOT{isEnabled:true}]->(timeSlot:TimeSlot{timeSlotType:type.timeSlotType})\n"+
            "return {id:id(timeSlot),name:timeSlot.name,startHour:r.startHour,startMinute:r.startMinute,endHour:r.endHour,endMinute:r.endMinute,isShiftStartTime:r.isShiftStartTime} as timeSlot order by r.startHour")
    List<Map<String,Object>> getUnitCurrentTimeSlots(long unitId);

}
