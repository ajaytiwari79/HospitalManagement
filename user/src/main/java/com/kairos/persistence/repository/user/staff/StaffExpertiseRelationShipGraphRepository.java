package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.staff.SectorAndStaffExpertiseQueryResult;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;
import com.kairos.persistence.model.staff.StaffExpertiseRelationShip;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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


    @Query("MATCH (staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) WHERE id(staff) = {0} AND id(expertise) IN {1}" +
            "RETURN id(rel) as id, id(expertise) as expertiseId, expertise.name as name,rel.expertiseStartDate as expertiseStartDate,rel.relevantExperienceInMonths as relevantExperienceInMonths")
    List<StaffExperienceInExpertiseDTO> getExpertiseWithExperienceByStaffIdAndExpertiseIds(Long staffId, List<Long> expertiseId);

    @Query("MATCH (staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) where id(staff) = {0} " +
            "MATCH (expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel) " +
            "MATCH(expertise)-[:"+BELONGS_TO_SECTOR+"]-(sector:Sector) " +
            "MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService) where id(orgService) IN {1}\n" +
            "MATCH(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(l:Level) where id(l) = {2} " +
            "WITH sector,expertise, staff,rel,seniorityLevel ORDER By seniorityLevel.from " +
            "WITH expertise ,staff,rel,collect({id:id(seniorityLevel),from:seniorityLevel.from,to:seniorityLevel.to}) as seniorityLevels,sector " +
            "OPTIONAL MATCH(expertise)<-[expRel:"+HAS_EXPERTISE_IN+"]-(up:UnitPosition)<-["+BELONGS_TO_STAFF+"]-(staff) " +
            "WITH expertise ,seniorityLevels,sector,CASE WHEN count(expRel)>0 THEN true ELSE false END as unitPositionExists,rel ORDER BY rel.expertiseStartDate " +
            "RETURN id(sector) as id,sector.name as name, COLLECT({id:id(rel), expertiseId:id(expertise), name:expertise.name ,expertiseStartDate:rel.expertiseStartDate ,relevantExperienceInMonths:rel.relevantExperienceInMonths ,unitPositionExists:unitPositionExists,seniorityLevels:seniorityLevels}) as expertiseWithExperience ")
    List<SectorAndStaffExpertiseQueryResult> getSectorWiseExpertiseWithExperienceByServiceIdsAndLevelId(Long staffId,List<Long> serviceIds,Long levelId);

   @Query("MATCH (staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) where id(staff) = {0} " +
            "MATCH (expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel) " +
            "MATCH(expertise)-[:"+BELONGS_TO_SECTOR+"]-(sector:Sector) " +
            "MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService) where id(orgService) IN {1}\n" +
            "WITH sector,expertise, staff,rel,seniorityLevel ORDER By seniorityLevel.from " +
            "WITH expertise ,staff,rel,collect({id:id(seniorityLevel),from:seniorityLevel.from,to:seniorityLevel.to}) as seniorityLevels,sector " +
            "OPTIONAL MATCH(expertise)<-[expRel:"+HAS_EXPERTISE_IN+"]-(up:UnitPosition)<-["+BELONGS_TO_STAFF+"]-(staff) " +
            "WITH expertise ,seniorityLevels,sector,CASE WHEN count(expRel)>0 THEN true ELSE false END as unitPositionExists,rel ORDER BY rel.expertiseStartDate " +
            "RETURN id(sector) as id,sector.name as name, COLLECT({id:id(rel), expertiseId:id(expertise), name:expertise.name ,expertiseStartDate:rel.expertiseStartDate ,relevantExperienceInMonths:rel.relevantExperienceInMonths ,unitPositionExists:unitPositionExists,seniorityLevels:seniorityLevels}) as expertiseWithExperience ")
    List<SectorAndStaffExpertiseQueryResult> getSectorWiseExpertiseWithExperienceByServiceIds(Long staffId,List<Long> serviceIds);




}
