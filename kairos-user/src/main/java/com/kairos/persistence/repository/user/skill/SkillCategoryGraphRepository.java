package com.kairos.persistence.repository.user.skill;

import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.skill.SkillCategory;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 15/9/16.
 */
@Repository
public interface SkillCategoryGraphRepository extends Neo4jBaseRepository<SkillCategory,Long>{

    /**
     * @return List all SkillCategory
     */
    @Query("Match (sc:SkillCategory) where sc.isEnabled=true return distinct sc")
    List<SkillCategory> findAll();


    /**
     * @param id
     * @return  List all SkillCategory by CountryId
     */
    @Query("MATCH (s:SkillCategory)-[:BELONGS_TO]->(c:Country) where id(c)={0} " +
            "AND s.isEnabled=true  with s as sc  " +
            "OPTIONAL MATCH (s:Skill)-[:HAS_CATEGORY]->(sc) WHERE s.isEnabled=true " +
            "return  { skillList: case when s is NULL then [] else collect({ " +
            "  id:id(s), " +
            "  name:s.name,  " +
            "  visitourId:s.visitourId,  " +
            "  shortName:s.shortName,  " +
            "  description:s.description}) END , " +
            "name:sc.name, " +
            "id:id(sc),  " +
            "description:sc.description " +
            "}AS result")
    List<Map<String,Object>> findSkillCategoryByCountryId(Long id);

    @Query("MATCH (s:Skill)-[:HAS_CATEGORY]->(sc:SkillCategory) where id(sc)={0} AND s.isEnabled=true  return s")
    List<Skill> getThisCategorySkills(long id);

    @Query("MATCH (s:Skill) where id(s)={0} MATCH (s)-[:HAS_CATEGORY]->(skillCategory:SkillCategory) return skillCategory")
    SkillCategory getSkillCategoryBySkillId(long skillId);


    @Query("MATCH (sc:SkillCategory {isEnabled:true})-[:BELONGS_TO]->(c:Country) WHERE id(c)={0} AND sc.name=~ {1} return sc")
    List<SkillCategory> checkDuplicateSkillCategory(long countryId, String name);

}
