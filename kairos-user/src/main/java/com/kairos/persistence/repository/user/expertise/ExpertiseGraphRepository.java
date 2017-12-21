package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.ExpertiseDTO;
import com.kairos.persistence.model.user.expertise.ExpertiseSkillQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;
import static com.kairos.persistence.model.constants.RelationshipConstants.EXPERTISE_HAS_SKILLS;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_CATEGORY;


/**
 * Created by prabjot on 28/10/16.
 */
@Repository
public interface ExpertiseGraphRepository extends Neo4jBaseRepository<Expertise,Long> {

    @Query("MATCH (country:Country) where id(country)={0} MATCH (country)<-[:BELONGS_TO]-(expertise:Expertise{isEnabled:true}) return expertise")
    List<Expertise> getAllExpertiseByCountry(long countryId);

    @Override
    @Query("MATCH (expertise:Expertise{isEnabled:true}) return expertise")
    List<Expertise> findAll();

    @Query("Match (expertise:Expertise)-[r:"+EXPERTISE_HAS_SKILLS+"]->(skill:Skill) where id(expertise)={0} AND id(skill)={1} return count(r) as countOfRel")
    int expertiseHasAlreadySkill(long expertiseId, long skillId);

    @Query("Match (expertise:Expertise),(skill:Skill) where id (expertise)={0} AND id(skill)={1} create (expertise)-[r:"+EXPERTISE_HAS_SKILLS+"{creationDate:{2},lastModificationDate:{3},isEnabled:true}]->(skill) return skill")
    void addSkillInExpertise(long expertiseId, long skillId, long creationDate, long lastModificationDate);

    @Query("Match (expertise:Expertise),(skill:Skill) where id (expertise)={0} AND id(skill) = {1} Match (expertise)-[r:"+EXPERTISE_HAS_SKILLS+"]->(skill) set r.lastModificationDate={2},r.isEnabled=true return skill")
    void updateExpertiseSkill(long expertiseId, long skillId, long lastModificationDate);

    @Query("Match (expertise:Expertise),(skill:Skill) where id(expertise)={0} AND id(skill) IN {1} match (expertise)-[r:"+EXPERTISE_HAS_SKILLS+"]->(skill) set r.isEnabled=false,r.lastModificationDate={2} return r")
    void deleteExpertiseSkill(long expertiseId, List<Long> skillId, long lastModificationDate);

    @Query("Match (expertise:Expertise) where id(expertise)={0} with expertise\n" +
            "Match (skillCategory:SkillCategory{isEnabled:true})-[:"+BELONGS_TO+"]->(country:Country) where id(country)={1} with skillCategory,expertise\n" +
            "Match (skill:Skill{isEnabled:true})-[:"+HAS_CATEGORY+"]->(skillCategory) with skill,skillCategory,expertise\n" +
            "optional Match (expertise)-[r:"+EXPERTISE_HAS_SKILLS+"]->(skill) with collect({id:id(skill),name:skill.name,isSelected:case when r.isEnabled then true else false end}) as skill,skillCategory\n" +
            "return collect({id:id(skillCategory),name:skillCategory.name,children:skill}) as skills")
    ExpertiseSkillQueryResult getExpertiseSkills(long expertiseId, long countryId);

    @Query("match (e:Expertise{isEnabled:true}) where id(e) in {0} \n" +
            "return count (e) as totalMatched")
    Long findAllExpertiseCountMatchedByIds(List<Long> ids);

    @Query("match (e:Expertise{isEnabled:true}) where id(e) in {0} \n" +
            "return id(e) as id,e.name as name,e.description as description")
    List<ExpertiseDTO> getAllFreeExpertises(List<Long> ids);

    @Query("match (e:Expertise{isEnabled:true})-[:"+BELONGS_TO+"]->(country:Country) where id(country) = {0} return e LIMIT 1")
    Expertise getExpertiesByCountry(Long id);

}
