package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.staff.StaffExpertiseWrapperQueryResult;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;
import com.kairos.persistence.model.staff.StaffExpertiseQueryResult;
import com.kairos.persistence.model.staff.StaffExpertiseRelationShip;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pavan on 27/3/18.
 */
@Repository
public interface StaffExpertiseRelationShipGraphRepository extends Neo4jBaseRepository<StaffExpertiseRelationShip, Long> {

    @Query("MATCH (staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) where id(staff) = {0}" +
            " return id(rel) as id, id(expertise) as expertiseId, expertise.name as name,rel.expertiseStartDate as expertiseStartDate,rel.relevantExperienceInMonths as relevantExperienceInMonths ")
    List<StaffExperienceInExpertiseDTO> getExpertiseWithExperienceByStaffId(Long staffId);

    @Query("MATCH (staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) where id(staff) = {0} AND id(expertise)={1}" +
            " return id(rel) as id, id(expertise) as expertiseId, expertise.name as name,rel.expertiseStartDate as expertiseStartDate,rel.relevantExperienceInMonths as relevantExperienceInMonths")
    StaffExperienceInExpertiseDTO getExpertiseWithExperienceByStaffIdAndExpertiseId(Long staffId, Long expertiseId);

    @Query("MATCH (staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) where id(staff) = {0} return expertise")
    List<Expertise> getAllExpertiseByStaffId(Long staffId);

    @Query("MATCH (staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) where id(staff) = {0} AND NOT id(expertise) IN{1} detach delete rel")
    void unlinkExpertiseFromStaffExcludingCurrent(Long staffId, List<Long> ids);

    @Query("MATCH (staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) where id(staff) = {0}" +
            " MATCH (expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel) " +
            " with expertise ,rel,seniorityLevel ORDER By seniorityLevel.from with expertise ,rel,collect(seniorityLevel) as seniorityLevels " +
            " return id(rel) as id, id(expertise) as expertiseId, expertise.name as name,rel.expertiseStartDate as expertiseStartDate,rel.relevantExperienceInMonths as relevantExperienceInMonths,seniorityLevels as seniorityLevels ")
    List<StaffExpertiseQueryResult> getExpertiseWithExperience(Long staffId);

    @Query("MATCH (staff:Staff) where id(staff) IN {0} " +
            "OPTIONAL MATCH(staff)-[rel:"+STAFF_HAS_EXPERTISE+"]->(expertise:Expertise) " +
            "OPTIONAL MATCH(staff)-[:"+BELONGS_TO_STAFF+"]->(unitPosition:UnitPosition)-[:"+HAS_EMPLOYMENT_TYPE+"]-(employmentType:EmploymentType) where unitPosition.startDateMillis<={1} AND  (unitPosition.endDateMillis IS NULL or unitPosition.endDateMillis>={1}) " +
            "return id(staff) as staffId,collect(id(expertise)) as expertiseIds,id(employmentType) as employmentTypeId")
    List<StaffExpertiseWrapperQueryResult> getStaffDetailByIds(Set<Long> staffId, Long currentMillis);


}
