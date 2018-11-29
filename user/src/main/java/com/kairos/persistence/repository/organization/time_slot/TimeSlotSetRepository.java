package com.kairos.persistence.repository.organization.time_slot;

import com.kairos.enums.TimeSlotType;
import com.kairos.persistence.model.organization.time_slot.TimeSlotSet;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_TIME_SLOT_SET;

/**
 * Created by prabjot on 6/12/17.
 */
@Repository
public interface TimeSlotSetRepository extends Neo4jBaseRepository<TimeSlotSet,Long> {


    @Query("Match (org:Organization)-[:"+HAS_TIME_SLOT_SET+"]->(timeSlotSet:TimeSlotSet) where id(org)={0} and date(timeSlotSet.startDate)>{1} " +
            "return timeSlotSet order by timeSlotSet.startDate limit 1")
    TimeSlotSet findOneByStartDateAfter(Long unitId,LocalDate endDate);

    @Query("Match (org:Organization)-[:"+HAS_TIME_SLOT_SET+"]->(timeSlotSet:TimeSlotSet) where id(org)={0} AND " +
            "(date(timeSlotSet.startDate)>={1} AND date(timeSlotSet.startDate) < {2}) AND timeSlotSet.timeSlotType={3} " +
            "return timeSlotSet order by timeSlotSet.startDate")
    List<TimeSlotSet> findTimeSlotSetByStartDateBetween(Long unitId, LocalDate startDate, LocalDate endDate, TimeSlotType timeSlotType);

}
