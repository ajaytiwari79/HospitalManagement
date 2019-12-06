package com.kairos.persistence.repository.user.skill;

import com.kairos.enums.SkillLevel;
import com.kairos.persistence.model.auth.StaffSkillLevelRelationship;
import com.kairos.persistence.model.user.expertise.response.SkillLevelQueryResult;
import com.kairos.persistence.model.user.expertise.response.SkillQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANISATION_HAS_SKILL;
import static com.kairos.persistence.model.constants.RelationshipConstants.STAFF_HAS_SKILLS;

/**
 * Created by oodles on 2/11/16.
 */
@Repository
public interface UserSkillLevelRelationshipGraphRepository extends Neo4jBaseRepository<StaffSkillLevelRelationship,Long> {

    @Query("MATCH (staff:Staff),(skill:Skill) where id(staff)={0} AND id(skill)={1} with staff,skill MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill) SET r.skillLevel={2},r.startDate={3},r.endDate={4},r.isEnabled={5} return r")
    void updateStaffSkill(long staffId, long skillId, SkillLevel skillLevel, long startDate, long endDate, boolean status);

    @Query("Match (staff:Staff),(skill:Skill) where id(staff)={0} AND id(skill) IN {1} with staff,skill\n" +
            "Match (unit:Unit)-[orgSkillRelation:"+ORGANISATION_HAS_SKILL+"{isEnabled:true}]->(skill) WHERE id(unit) = {2} with staff,skill,orgSkillRelation\n" +
            "MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill)-[:HAS_CATEGORY]->(skillCategory:SkillCategory)\n" +
            "return {name:case when orgSkillRelation is null or orgSkillRelation.customName is null then skill.name else orgSkillRelation.customName end,skillId:id(skill),startDate:r.startDate,endDate:r.endDate,level:r.skillLevel,skillCategory:skillCategory.name,status:r.isEnabled} as data")
    List<Map<String,Object>> getStaffSkillRelationship(long staffId, List<Long> skillId, long unitId);

    @Query("MATCH (staff)-[r:"+STAFF_HAS_SKILLS+"]->(skill) DETACH DELETE r")
    void removeExistingByStaffIdAndSkillId(Long staffId,Long skillId);

    @Query("MATCH (staff)-[r:"+STAFF_HAS_SKILLS+"]->(skill:Skill) WHERE id(skill) IN {1} AND id(staff)={0} \n " +
            "WITH skill,{}" +
            "return id(skill) as id")
    List<SkillQueryResult> findAllByStaffIdAndSkillIds(Long staffId, List<Long> skillId);

    @Query("MATCH (staff)-[r:"+STAFF_HAS_SKILLS+"]->(skill:Skill) WHERE id(skill) = {1} AND id(staff)={0} \n " +
            "return DISTINCT r.skillLevel as skillLevel,r.startDate as startDate,r.endDate AS endDate")
    Set<SkillLevelQueryResult> getSkillLevel(Long staffId, Long skillId);

}
