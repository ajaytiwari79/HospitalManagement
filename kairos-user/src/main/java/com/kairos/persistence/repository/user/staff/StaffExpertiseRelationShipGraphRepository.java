package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.user.staff.StaffExpertiseRelationShip;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by pavan on 27/3/18.
 */
@Repository
public interface StaffExpertiseRelationShipGraphRepository extends Neo4jBaseRepository<StaffExpertiseRelationShip, Long> {
    @Query("MATCH(staff:Staff)-[rel:STAFF_HAS_EXPERTISE]-(expertise:Expertise) where id(staff) = {0} " +
            "DETACH delete rel")
    void unlinkPreviousExpertise(Long staffId);
}
