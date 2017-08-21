package com.kairos.persistence.repository.organization;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.organization.OrganizationTimeSlotRelationship;
import com.kairos.persistence.model.organization.TimeSlot;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 23/1/17.
 */
@Repository
public interface OrganizationTimeSlotGraphRepository extends GraphRepository<OrganizationTimeSlotRelationship> {

    @Query("Match (n:Organization)-[r:"+ORGANIZATION_TIME_SLOT+"{isEnabled:true}]->(timeSlot:TimeSlot{timeSlotType:{1}}) where id(n)={0} return {id:id(timeSlot),startHour:r.startHour,endHour:r.endHour,startMinute:r.startMinute,endMinute:r.endMinute} as data")
    List<Map<String,Object>> getOrganizationTimeSlots(long unitId, TimeSlot.TYPE type);


}
