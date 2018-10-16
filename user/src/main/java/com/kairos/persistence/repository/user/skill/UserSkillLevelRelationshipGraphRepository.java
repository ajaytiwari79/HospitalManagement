package com.kairos.persistence.repository.user.skill;
import com.kairos.persistence.model.auth.StaffSkillLevelRelationship;
import com.kairos.persistence.model.user.skill.Skill;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANISATION_HAS_SKILL;

/**
 * Created by oodles on 2/11/16.
 */
@Repository
public interface UserSkillLevelRelationshipGraphRepository extends Neo4jBaseRepository<StaffSkillLevelRelationship,Long> {

    @Query("MATCH (staff:Staff),(skill:Skill) where id(staff)={0} AND id(skill)={1} with staff,skill MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill) SET r.skillLevel={2},r.startDate={3},r.endDate={4},r.isEnabled={5} return r")
    void updateStaffSkill(long staffId, long skillId, Skill.SkillLevel skillLevel, long startDate, long endDate, boolean status);

    @Query("Match (staff:Staff),(skill:Skill) where id(staff)={0} AND id(skill) IN {1} with staff,skill\n" +
            "MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill)-[:HAS_CATEGORY]->(skillCategory:SkillCategory)\n" +
            "return {name:skill.name,skillId:id(skill),startDate:r.startDate,endDate:r.endDate,level:r.skillLevel,skillCategory:skillCategory.name,status:r.isEnabled} as data")
    List<Map<String,Object>> getStaffSkillRelationship(long staffId, List<Long> skillId);

    @Query("Match (staff:Staff),(skill:Skill) where id(staff)={0} AND id(skill) IN {1} with staff,skill\n" +
            "Match (unit:Organization)-[orgSkillRelation:"+ORGANISATION_HAS_SKILL+"{isEnabled:true}]->(skill) WHERE id(unit) = {2} with staff,skill,orgSkillRelation\n" +
            "MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill)-[:HAS_CATEGORY]->(skillCategory:SkillCategory)\n" +
            "return {name:case when orgSkillRelation is null or orgSkillRelation.customName is null then skill.name else orgSkillRelation.customName end,skillId:id(skill),startDate:r.startDate,endDate:r.endDate,level:r.skillLevel,skillCategory:skillCategory.name,status:r.isEnabled} as data")
    List<Map<String,Object>> getStaffSkillRelationship(long staffId, List<Long> skillId, long unitId);


}
