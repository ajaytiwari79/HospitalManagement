package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.*;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 28/10/16.
 */
@Repository
public interface ExpertiseGraphRepository extends Neo4jBaseRepository<Expertise, Long> {

    @Query("MATCH (country:Country) where id(country)={0} MATCH (country)<-[:BELONGS_TO]-(expertise:Expertise{isEnabled:true}) return expertise")
    List<Expertise> getAllExpertiseByCountry(long countryId);

    @Query("MATCH (country:Country) where id(country)={0} MATCH (country)<-[:BELONGS_TO]-(expertise:Expertise{isEnabled:true}) return expertise LIMIT 1")
    Expertise getOneDefaultExpertiseByCountry(long countryId);

    /*@Query("MATCH (country:Country) where id(country)={0} MATCH (country)<-[:BELONGS_TO]-(expertise:Expertise{isEnabled:true}) return expertise")*/
    @Query("MATCH (country:Country) where id(country)={0} MATCH (country)<-[:BELONGS_TO]-(expertise:Expertise{isEnabled:true}) with expertise, country \n" +
            "OPTIONAL MATCH (expertise)-[:HAS_TAG]-(tag:Tag)<-[:COUNTRY_HAS_TAG]-(country) WHERE tag.deleted=false AND tag.masterDataType='EXPERTISE' with expertise,tag\n" +
            "RETURN id(expertise) as id, expertise.name as name, expertise.description as description,CASE when tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag})  END as tags")
    List<ExpertiseTagDTO> getAllExpertiseWithTagsByCountry(long countryId);

    @Override
    @Query("MATCH (expertise:Expertise{isEnabled:true}) return expertise")
    List<Expertise> findAll();

    @Query("Match (expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "]->(skill:Skill) where id(expertise)={0} AND id(skill)={1} return count(r) as countOfRel")
    int expertiseHasAlreadySkill(long expertiseId, long skillId);

    @Query("Match (expertise:Expertise),(skill:Skill) where id (expertise)={0} AND id(skill)={1} create (expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{creationDate:{2},lastModificationDate:{3},isEnabled:true}]->(skill) return skill")
    void addSkillInExpertise(long expertiseId, long skillId, long creationDate, long lastModificationDate);

    @Query("Match (expertise:Expertise),(skill:Skill) where id (expertise)={0} AND id(skill) = {1} Match (expertise)-[r:" + EXPERTISE_HAS_SKILLS + "]->(skill) set r.lastModificationDate={2},r.isEnabled=true return skill")
    void updateExpertiseSkill(long expertiseId, long skillId, long lastModificationDate);

    @Query("Match (expertise:Expertise),(skill:Skill) where id(expertise)={0} AND id(skill) IN {1} match (expertise)-[r:" + EXPERTISE_HAS_SKILLS + "]->(skill) set r.isEnabled=false,r.lastModificationDate={2} return r")
    void deleteExpertiseSkill(long expertiseId, List<Long> skillId, long lastModificationDate);

    @Query("Match (expertise:Expertise) where id(expertise)={0} with expertise\n" +
            "Match (skillCategory:SkillCategory{isEnabled:true})-[:" + BELONGS_TO + "]->(country:Country) where id(country)={1} with skillCategory,expertise,country\n" +
            "Match (skill:Skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory) with skill,skillCategory,expertise,country\n" +
            "OPTIONAL MATCH (skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[" + COUNTRY_HAS_TAG + "]-(country)  with skill,skillCategory,expertise, CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as tags\n" +
            "optional Match (expertise)-[r:" + EXPERTISE_HAS_SKILLS + "]->(skill) with collect({id:id(skill),name:skill.name,isSelected:case when r.isEnabled then true else false end, tags:tags}) as skill,skillCategory\n" +
            "return collect({id:id(skillCategory),name:skillCategory.name,children:skill}) as skills")
    ExpertiseSkillQueryResult getExpertiseSkills(long expertiseId, long countryId);

    @Query("match (e:Expertise{isEnabled:true}) where id(e) in {0} \n" +
            "return count (e) as totalMatched")
    Long findAllExpertiseCountMatchedByIds(List<Long> ids);

    @Query("match (e:Expertise{isEnabled:true}) where id(e) in {0} \n" +
            "return id(e) as id,e.name as name,e.description as description")
    List<ExpertiseDTO> getAllFreeExpertises(List<Long> ids);

    @Query("match (e:Expertise{isEnabled:true})-[:" + BELONGS_TO + "]->(country:Country) where id(country) = {0} return e LIMIT 1")
    Expertise getExpertiesByCountry(Long id);


    @Query("MATCH (org:Organization) - [:" + BELONGS_TO + "] -> (country)<-[:BELONGS_TO]-(expertise:Expertise{isEnabled:true})  where id(org)={0} return expertise")
    List<Expertise> getAllExpertiseByOrganizationId(long unitId);


    @Query("match (e:Expertise{isEnabled:true})-[:" + BELONGS_TO + "]->(country:Country) where id(country) = {0} AND id(e) = {1} return e")
    Expertise getExpertiesOfCountry(Long countryId, Long expertiseId);

    //TODO need to add publish filter as well
    @Query("match (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false}) where id(country) = {0}" +
            "match(expertise)-[:SUPPORTS_SERVICE]-(orService:OrganizationService)\n" +
            "match(expertise)-[:IN_ORGANIZATION_LEVEL]-(level:Level)\n" +
            "match(expertise)-[:HAS_PAY_TABLE]-(payTable:PayTable)\n" +
            "match(expertise)-[:SUPPORTS_UNION]-(union:Organization)\n" +
            "match(expertise)-[:FOR_SENIORITY_LEVEL]->(seniorityLevel:SeniorityLevel)-[rel:HAS_FUNCTION]->(functions:Function) \n" +
            "with expertise,payTable,union,level,orService,seniorityLevel,collect({name:functions.name,id:id(functions),amount:rel.amount }) as functionData " +
            "match(sl)-[:PAY_GROUP_AREAS]-(pga:PayGroupArea)" +
            "with expertise,payTable,union,level,orService,seniorityLevel,functionData,collect  (distinct{name:pga.name,id:id(pga)}) as payGroupAreas"+
            "with expertise,payTable,union,level,orService,seniorityLevel,functionData,collect({name:pga.name,id:id(pga)}) as payGroupAreas " +
            "return expertise.name as name ,id(expertise) as id,expertise.paidOutFrequency as paidOutFrequency ,expertise.startDateMillis as startDateMillis ," +
            "expertise.endDateMillis as endDateMillis ,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek,expertise.description as description ,expertise.published as published," +
            "{id:id(orService),name:orService.name} as organizationService,{id:id(level),name:level.name} as organizationLevel,{id:id(payTable),name:payTable.name} as payTable,{id:id(union),name:union.name} as union,"
            + " CASE when seniorityLevel IS NULL THEN [] ELSE collect({from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,basePayGrade:seniorityLevel.basePayGrade,moreThan:seniorityLevel.moreThan,functions:functionData,payGroupAreas:payGroupAreas})  END  as seniorityLevel")
    List<ExpertiseQueryResult> getAllExpertiseByCountryId(long countryId);

    /*match(p)-[:FOR_SENIORITY_LEVEL]-(sl:SeniorityLevel)
 match(sl)-[rel:HAS_FUNCTION]-(fn:Function)
 with p, fn,rel,sl,collect({name:fn.name,id:id(fn),amount:rel.amount }) as function
 optional match(sl)-[:PAY_GROUP_AREAS]-(pga:PayGroupArea)
 with p, fn,rel,sl,function,collect({name:pga.name,id:id(pga)}) as payGR
return p,{name:sl.name,functions:function,pga:payGR  } as sl*/
}
