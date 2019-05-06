package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.organization.StaffTeamRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRelationshipGraphRespository extends Neo4jBaseRepository<StaffTeamRelationship,Long> {
}
