package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.OrganizationTimeSlotRelationship;
import com.kairos.persistence.model.organization.TimeSlot;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION_TIME_SLOT;


/**
 * Created by prabjot on 23/1/17.
 */
@Repository
public interface OrganizationTimeSlotGraphRepository extends Neo4jBaseRepository<OrganizationTimeSlotRelationship,Long> {

    @Query("Match (n:Organization)-[r:"+ORGANIZATION_TIME_SLOT+"{isEnabled:true}]->(timeSlot:TimeSlot{timeSlotType:{1}}) where id(n)={0} return {id:id(timeSlot),startHour:r.startHour,endHour:r.endHour,startMinute:r.startMinute,endMinute:r.endMinute} as data")
    List<Map<String,Object>> getOrganizationTimeSlots(long unitId, TimeSlot.TYPE type);


}
