package com.kairos.persistence.repository.organization.time_slot;

import com.kairos.persistence.model.organization.time_slot.TimeSlotSetTimeSlotRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeSlotRelationshipGraphRepository extends Neo4jBaseRepository<TimeSlotSetTimeSlotRelationship,Long> {
}
