package com.kairos.persistence.repository.user.skill;

import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.skill.SkillCategory;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANISATION_HAS_SKILL;

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

    @Query("MATCH (s:SkillCategory)-[:BELONGS_TO]->(c:Country) where id(c)={0} AND s.isEnabled=true  with s as sc,c  OPTIONAL MATCH (s:Skill)-[:HAS_CATEGORY]->(sc) WHERE s.isEnabled=true with c,sc,s \n" +
            " OPTIONAL MATCH (s)-[r:HAS_TAG]->(t:Tag)<-[:COUNTRY_HAS_TAG]-(c) WHERE t.masterDataType='SKILL' AND t.countryTag=true AND t.deleted =false with CASE when t IS NULL THEN [] ELSE collect({id:id(t),name:t.name,countryTag:t.countryTag})   END as tags,sc,s\n" +
            "return  { skillList: case when s is NULL then [] else collect({   \n" +
            "id:id(s),   \n" +
            "name:s.name, \n" +
            "tags:tags,\n" +
            "visitourId:s.visitourId, \n" +
            "shortName:s.shortName, \n" +
            "description:s.description}) END ,\n" +
            "name:sc.name, \n" +
            "id:id(sc),  \n" +
            "description:sc.description} AS result")
    List<Map<String,Object>> findSkillCategoryByCountryId(Long id);

    @Query("MATCH (s:Skill)<-[:"+ORGANISATION_HAS_SKILL+"]-(organization:Unit) where id(organization)={0} AND s.isEnabled=true " +
            " OPTIONAL MATCH (s)-[:HAS_CATEGORY]->(sc:SkillCategory) WHERE sc.isEnabled=true with sc,s \n" +
            "return  { skillList: case when s is NULL then [] else collect({   \n" +
            "id:id(s), name:s.name, shortName:s.shortName, description:s.description}) END ,\n" +
            "name:sc.name, id:id(sc), description:sc.description} AS result")
    List<Map<String,Object>> findSkillCategoryByUnitId(Long id);

    @Query("MATCH (s:Skill)-[:HAS_CATEGORY]->(sc:SkillCategory) where id(sc)={0} AND s.isEnabled=true  return s")
    List<Skill> getThisCategorySkills(long id);

    @Query("MATCH (s:Skill) where id(s)={0} MATCH (s)-[:HAS_CATEGORY]->(skillCategory:SkillCategory) return skillCategory")
    SkillCategory getSkillCategoryBySkillId(long skillId);


    @Query("MATCH (sc:SkillCategory {isEnabled:true})-[:BELONGS_TO]->(c:Country) WHERE id(c)={0} AND sc.name=~ {1} return sc")
    List<SkillCategory> checkDuplicateSkillCategory(long countryId, String name);

    @Query("MATCH (skillCategory:SkillCategory {isEnabled:true})-[:BELONGS_TO]->(country:Country) WHERE id(country)={0} AND skillCategory.name=~ {1} return skillCategory")
    SkillCategory findByNameIgnoreCaseAndIsEnabledTrue(long countryId,String name);

}
