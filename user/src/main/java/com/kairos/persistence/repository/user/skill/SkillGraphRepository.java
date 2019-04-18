package com.kairos.persistence.repository.user.skill;

import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.skill.SkillCategory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * SkillGraphRepository
 */
@Repository
public interface SkillGraphRepository extends Neo4jBaseRepository<Skill,Long>{


    /**
     * @return List of Skill
     */
    @Query("MATCH (s:Skill),(sc:SkillCategory) where s.isEnabled= true return s ORDER BY s.SkillCategory.name")
    List<Skill> findAll();

    @Query("MATCH (s:Skill) where id(s) IN {0} return s")
    List<Skill> findById(List<Long> ids);

    @Query("MATCH (s:Skill  {isEnabled:true} )-[:HAS_CATEGORY]->(sc:SkillCategory) WHERE id(sc)={0} AND s.name=~ {1} return s")
    List<Skill> checkDuplicateSkill(long categoryId, String name);

    /**
     *
     * @param id
     * @return List of Skills by SkillCategoryID
     */
    @Query("MATCH (skill:Skill)-[:HAS_CATEGORY]->(skillCategory:SkillCategory) where id(skillCategory)={0} AND  skill.isEnabled= true return skill")
    List<Skill> skillsByCategoryId(Long id);


    /**
     *
     * @param aLong
     */
    @Query("MATCH (s:Skill) where id(s) = {0} DETACH DELETE s")
    void delete(Long aLong);


    /**
     * Set Enable false to a given skill
     * @return
     */
    @Query("MATCH (sc:SkillCategory), (s:Skill) MATCH (sc)-[:CONTAINS_SKILL]-(s) WHERE  id(sc) = {0} AND id(s) = {1}  SET s.isEnabled =false RETURN sc")
    SkillCategory safeDelete(Long categoryId, Long skillId);

    @Query("MATCH (skill)-[:HAS_CATEGORY]->(skillCategory:SkillCategory) return {id:id(skillCategory),name:skillCategory.name,children:collect({id:id(skill),name:skill.name,parentId:id(skillCategory)})} AS skillList")
    List<Map<String,Object>> getAllSkills();

    @Query("MATCH (skill:Skill)-[:HAS_CATEGORY]->(skillCategory:SkillCategory)-[:BELONGS_TO]-(country:Country) where id(country) = {0} return {skills: case when skill is NULL then [] else collect({id:id(skill),name:skill.name,description:skill.description}) END,id:id(skillCategory),name:skillCategory.name,description:skillCategory.description} AS result")
    List<Map<String,Object>> getSkillsByCountryId(long countryId);

    @Query("MATCH (skill{isEnabled:true})-[:HAS_CATEGORY]->(skillCategory:SkillCategory{isEnabled:true})-[:BELONGS_TO]->(country:Country) where id(country)={0} return {id:id(skillCategory),name:skillCategory.name,skills:collect({id:id(skill),name:skill.name,parentId:id(skillCategory)})} AS data")
    List<Map<String,Object>> getSkillsByCountryForTaskType(long countryId);

    @Query("MATCH (skill{isEnabled:true})-[:HAS_CATEGORY]->(skillCategory:SkillCategory{isEnabled:true}) return {id:id(skillCategory),name:skillCategory.name,skills:collect({skillId:id(skill),name:skill.name,parentId:id(skillCategory)})} AS data")
    List<Map<String,Object>> getSkillsForTaskType();

    @Query("Match (organization:Organization)-[r:"+ORGANISATION_HAS_SKILL+"]->(skill:Skill) where id(organization)={0} AND id(skill)={1} set r.customName={2} return r is not null")
    boolean updateSkillOfOrganizationWithCustomName(long unitId, long skillId, String customName);

    @Query("Match (organization:Organization)-[r:"+ORGANISATION_HAS_SKILL+"]->(skill:Skill) where id(organization)={0} AND id(skill)={1} set  r.customName=skill.name return r is not null")
    boolean updateSkillOfOrganization(long unitId, long skillId);

    @Query("Match (skill:Skill)-[r:"+HAS_TAG+"]-(tag:Tag{countryTag:true}) WHERE id(skill) = {0} DELETE r ")
    void removeAllCountryTags(long skillId);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag) WHERE id(org)={0} with tag \n"+
            "Match (skill:Skill)-[skillTagRel:"+HAS_TAG+"]-(tag) WHERE id(skill) = {1} DELETE  skillTagRel ")
    void removeAllOrganizationTags(long orgId, long skillId);



    List<Skill> findSkillByNameIn(List<String> skillNames);

    List<Skill> findByExternalIdInAndIsEnabledTrue(List<String> timecareIds);

    @Query("MATCH (skill:Skill{isEnabled:true})-[:HAS_CATEGORY]->(skillCategory:SkillCategory)-[:"+BELONGS_TO+"]->(country:Country) where id(country)={0} return skill")
    List<Skill> findAllSkillsByCountryId(long countryId);
}
