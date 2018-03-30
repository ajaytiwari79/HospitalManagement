package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.staff.StaffExperienceInExpertiseDTO;
import com.kairos.persistence.model.user.staff.StaffExpertiseRelationShip;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.STAFF_HAS_EXPERTISE;

/**
 * Created by pavan on 27/3/18.
 */
@Repository
public interface StaffExpertiseRelationShipGraphRepository extends Neo4jBaseRepository<StaffExpertiseRelationShip, Long> {

    @Query("MATCH (staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) where id(staff) = {0}" +
            " return id(rel) as id, id(expertise) as expertiseId, expertise.name as name,rel.relevantExperienceInMonths as relevantExperienceInMonths ")
    List<StaffExperienceInExpertiseDTO> getExpertiseWithExperienceByStaffId(Long staffId);

    @Query("MATCH (staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) where id(staff) = {0} AND id(expertise)={1}" +
            " return id(rel) as id, id(expertise) as expertiseId, expertise.name as name,rel.relevantExperienceInMonths as relevantExperienceInMonths, rel.expertiseStartDate as expertiseStartDate")
    StaffExperienceInExpertiseDTO getExpertiseWithExperienceByStaffIdAndExpertiseId(Long staffId,Long expertiseId);

    @Query("MATCH (staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) where id(staff) = {0} return expertise")
    List<Expertise> getAllExpertiseByStaffId(Long staffId);

    @Query("MATCH (staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) where id(staff) = {0} AND id(expertise) <> IN{1} return expertise")
    void unlinkExpertiseFromStaffExcludingCurrent(Long staffId,List<Long> ids);
}
