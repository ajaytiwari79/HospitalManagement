package com.kairos.persistence.repository.organization.time_slot;

import com.kairos.persistence.model.organization.time_slot.TimeSlotSet;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_TIME_SLOT;

/**
 * Created by prabjot on 6/12/17.
 */
@Repository
public interface TimeSlotSetRepository extends GraphRepository<TimeSlotSet> {

    TimeSlotSet findByStartDateAfter(Date endDate, PageRequest pageRequest);

    List<TimeSlotSet> findByStartDateBetween(Date startDate, Date endDate, Sort sort);

    @Query("Match (timeSlotSet:TimeSlotSet)-[:r"+HAS_TIME_SLOT+"]->(timeSlot:TimeSlot) where timeSlotSet={0} delete r,timeSlot,timeSlotSet")
    void deleteTimeSlotSet(TimeSlotSet timeSlotSet);
}
