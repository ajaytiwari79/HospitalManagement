package com.kairos.persistence.repository.organization.time_slot;

import com.kairos.persistence.model.organization.time_slot.TimeSlotSet;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_TIME_SLOT_SET;

/**
 * Created by prabjot on 6/12/17.
 */
@Repository
public interface TimeSlotSetRepository extends GraphRepository<TimeSlotSet> {


    @Query("Match (org:Organization)-[:"+HAS_TIME_SLOT_SET+"]->(timeSlotSet:TimeSlotSet) where id(org)={0} and timeSlotSet.startDate>{1} " +
            "return timeSlotSet order by timeSlotSet.startDate limit 1")
    TimeSlotSet findOneByStartDateAfter(Long unitId,Date endDate);

    @Query("Match (org:Organization)-[:"+HAS_TIME_SLOT_SET+"]->(timeSlotSet:TimeSlotSet) where id(org)={0} and timeSlotSet.endDate>{1} " +
            "return timeSlotSet order by timeSlotSet.startDate limit 1")
    TimeSlotSet findOneByEndDateAfter(Long unitId,Date endDate);

    @Query("Match (org:Organization)-[:"+HAS_TIME_SLOT_SET+"]->(timeSlotSet:TimeSlotSet) where id(org)={0} AND " +
            "(timeSlotSet.startDate>{1} AND timeSlotSet.startDate < {2}) return timeSlotSet order by timeSlotSet.startDate")
    List<TimeSlotSet> findTimeSlotSetByStartDateBetween(Long unitId,Date startDate, Date endDate);

}
