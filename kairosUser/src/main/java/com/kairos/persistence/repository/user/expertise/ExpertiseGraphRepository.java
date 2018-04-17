package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.*;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 28/10/16.
 */
@Repository
public interface ExpertiseGraphRepository extends Neo4jBaseRepository<Expertise, Long> {

    @Query("MATCH (country:Country) where id(country)={0} MATCH (country)<-[:BELONGS_TO]-(expertise:Expertise{deleted:false,published:true}) return expertise")
    List<Expertise> getAllExpertiseByCountry(long countryId);

    @Query("MATCH (country:Country) where id(country)={0} MATCH (country)<-[:BELONGS_TO]-(expertise:Expertise{deleted:false,published:true}) return expertise LIMIT 1")
    Expertise getOneDefaultExpertiseByCountry(long countryId);

    /*@Query("MATCH (country:Country) where id(country)={0} MATCH (country)<-[:BELONGS_TO]-(expertise:Expertise{deleted:false}) return expertise")*/
    @Query("MATCH (country:Country) where id(country)={0} MATCH (country)<-[:BELONGS_TO]-(expertise:Expertise{deleted:false,published:true}) with expertise, country \n" +
            "OPTIONAL MATCH (expertise)-[:HAS_TAG]-(tag:Tag)<-[:COUNTRY_HAS_TAG]-(country) WHERE tag.deleted=false AND tag.masterDataType='EXPERTISE' with expertise,tag\n" +
            "RETURN id(expertise) as id, expertise.name as name, expertise.description as description,CASE when tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag})  END as tags")
    List<ExpertiseTagDTO> getAllExpertiseWithTagsByCountry(long countryId);

    @Override
    @Query("MATCH (expertise:Expertise{deleted:false,published:true}) return expertise")
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

    @Query("match (e:Expertise{deleted:false}) where id(e) in {0} \n" +
            "return count (e) as totalMatched")
    Long findAllExpertiseCountMatchedByIds(List<Long> ids);

    @Query("match (e:Expertise{deleted:false}) where id(e) in {0} \n" +
            "return id(e) as id,e.name as name,e.description as description")
    List<ExpertiseDTO> getAllFreeExpertises(List<Long> ids);

    @Query("match (e:Expertise{deleted:false,published:true})-[:" + BELONGS_TO + "]->(country:Country) where id(country) = {0} return e LIMIT 1")
    Expertise getExpertiesByCountry(Long id);


    @Query("MATCH (org:Organization) - [:" + BELONGS_TO + "] -> (country)<-[:BELONGS_TO]-(expertise:Expertise{deleted:false})  where id(org)={0} return expertise")
    List<Expertise> getAllExpertiseByOrganizationId(long unitId);


    @Query("match (e:Expertise{deleted:false,published:true})-[:" + BELONGS_TO + "]->(country:Country) where id(country) = {0} AND id(e) = {1} return e")
    Expertise getExpertiesOfCountry(Long countryId, Long expertiseId);


    //TODO need to add publish filter as well
    @Query("match (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,hasDraftCopy:false}) where id(country) = {0}" +
            "match(expertise)-[:" + SUPPORTS_SERVICE + "]-(orgService:OrganizationService)\n" +
            "match(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level)\n" +
            "match(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)\n" +
            "match(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable)" +
            "optional match(seniorityLevel)-[functionRelation:" + HAS_FUNCTION + "]->(functions:Function) \n" +
            "with expertise,payTable,union,level,orgService,seniorityLevel,payGradeData,CASE when functions IS NULL THEN [] ELSE collect({name:functions.name,id:id(functions),amount:functionRelation.amount }) END as functionData  \n" +
            "optional match(seniorityLevel)-[:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea)  \n" +
            "with expertise,payTable,union,level,orgService,seniorityLevel,functionData,payGradeData,CASE when pga IS NULL THEN [] ELSE  collect  (distinct{name:pga.name,id:id(pga)}) END as payGroupAreas    \n" +
            "return expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate,expertise.paidOutFrequency as paidOutFrequency ,expertise.startDateMillis as startDateMillis , expertise.hasVersion as hasVersion," +
            "expertise.endDateMillis as endDateMillis ,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek,expertise.description as description ,expertise.published as published," +
            "orgService as organizationService,level as organizationLevel,payTable as payTable,union as union,"
            + " CASE when seniorityLevel IS NULL THEN [] ELSE collect({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,functions:functionData,payGroupAreas:payGroupAreas,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels")
    List<ExpertiseQueryResult> getUnpublishedExpertise(long countryId);


    @Query("MATCH (expertise:Expertise)-[rel:" + HAS_DRAFT_EXPERTISE + "]-(parentExpertise:Expertise) where id(expertise)={0} \n" +
            " set expertise.endDateMillis={1} set expertise.hasDraftCopy=false set expertise.published=true detach delete rel")
    void setEndDateToExpertise(Long expertiseId, Long endDateMillis);


    @Query("match (e:Expertise)-[:" + HAS_DRAFT_EXPERTISE + "]->(expertise:Expertise) where id(e) = {0}" +
            "match(expertise)-[:" + SUPPORTS_SERVICE + "]-(orgService:OrganizationService)\n" +
            "match(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level)\n" +
            "match(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)\n" +
            "match(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable)" +
            "optional match(seniorityLevel)-[functionRelation:" + HAS_FUNCTION + "]->(functions:Function) \n" +
            "with expertise,payTable,union,level,orgService,seniorityLevel,payGradeData,CASE when functions IS NULL THEN [] ELSE collect({name:functions.name,id:id(functions),amount:functionRelation.amount }) END as functionData  \n" +
            "optional match(seniorityLevel)-[:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea)  \n" +
            "with expertise,payTable,union,level,orgService,seniorityLevel,functionData,payGradeData,CASE when pga IS NULL THEN [] ELSE  collect  (distinct{name:pga.name,id:id(pga)}) END as payGroupAreas    \n" +
            "return expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate,expertise.paidOutFrequency as paidOutFrequency ,expertise.startDateMillis as startDateMillis, expertise.hasVersion as hasVersion, " +
            "expertise.endDateMillis as endDateMillis ,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek,expertise.description as description ,expertise.published as published," +
            "orgService as organizationService,level as organizationLevel,payTable as payTable,union as union,"
            + " CASE when seniorityLevel IS NULL THEN [] ELSE collect({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,functions:functionData,payGroupAreas:payGroupAreas,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels order by expertise.creationDate")
    ExpertiseQueryResult getParentExpertiseByExpertiseId(Long expertiseId);


    @Query("match (expertise:Expertise{deleted:false}) where id(expertise) IN {0} \n" +
            "return id(expertise) as id,expertise.name as name,expertise.description as description")
    List<Expertise> getExpertiseByIdsIn(List<Long> ids);

    @Query("match (expertise:Expertise{deleted:false}) where id(expertise) = {0}" +
            "match(expertise)-[:" + SUPPORTS_SERVICE + "]-(orgService:OrganizationService)\n" +
            "match(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level)\n" +
            "match(expertise)-[:" + HAS_PAY_TABLE + "]-(payTable:PayTable)\n" +
            "match(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)\n" +
            "match(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable)" +
            "optional match(seniorityLevel)-[functionRelation:" + HAS_FUNCTION + "]->(functions:Function) \n" +
            "with expertise,payTable,union,level,orgService,seniorityLevel,payGradeData,CASE when functions IS NULL THEN [] ELSE collect({name:functions.name,id:id(functions),amount:functionRelation.amount }) END as functionData  \n" +
            "optional match(seniorityLevel)-[:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea)  \n" +
            "with expertise,payTable,union,level,orgService,seniorityLevel,functionData,payGradeData,CASE when pga IS NULL THEN [] ELSE  collect  (distinct{name:pga.name,id:id(pga)}) END as payGroupAreas    \n" +
            "return expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate,expertise.paidOutFrequency as paidOutFrequency ,expertise.startDateMillis as startDateMillis ,expertise.hasVersion as hasVersion," +
            "expertise.endDateMillis as endDateMillis ,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek,expertise.description as description ,expertise.published as published," +
            "orgService as organizationService,level as organizationLevel,payTable as payTable,union as union,"
            + " CASE when seniorityLevel IS NULL THEN [] ELSE collect({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,functions:functionData,payGroupAreas:payGroupAreas,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels order by expertise.creationDate")
    ExpertiseQueryResult getExpertiseById(Long expertiseId);

    @Query("match (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,published:true}) where id(country) = {0}" +
            "match(expertise)-[:" + SUPPORTS_SERVICE + "]-(orgService:OrganizationService)\n" +
            "match(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level)\n" +
            "match(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)\n" +
            "match(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable)" +
            "optional match(seniorityLevel)-[functionRelation:" + HAS_FUNCTION + "]->(functions:Function) \n" +
            "with expertise,payTable,union,level,orgService,seniorityLevel,payGradeData,CASE when functions IS NULL THEN [] ELSE collect({name:functions.name,id:id(functions),amount:functionRelation.amount }) END as functionData  \n" +
            "optional match(seniorityLevel)-[:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea)  \n" +
            "with expertise,payTable,union,level,orgService,seniorityLevel,functionData,payGradeData,CASE when pga IS NULL THEN [] ELSE  collect  (distinct{name:pga.name,id:id(pga)}) END as payGroupAreas    \n" +
            "return expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate,expertise.paidOutFrequency as paidOutFrequency ,expertise.startDateMillis as startDateMillis , expertise.hasVersion as hasVersion," +
            "expertise.endDateMillis as endDateMillis ,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek,expertise.description as description ,expertise.published as published," +
            "orgService as organizationService,level as organizationLevel,payTable as payTable,union as union,"
            + " CASE when seniorityLevel IS NULL THEN [] ELSE collect({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,functions:functionData,payGroupAreas:payGroupAreas,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels order by expertise.creationDate")
    List<ExpertiseQueryResult> getAllExpertiseByCountryId(Long countryId);


}
