package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.*;
import com.kairos.persistence.model.user.filter.FilterSelectionQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 28/10/16.
 */
@Repository
public interface ExpertiseGraphRepository extends Neo4jBaseRepository<Expertise, Long> {

    @Query("MATCH (country:Country) where id(country)={0}  " +
            "MATCH (country)<-[:"+BELONGS_TO+"]-(expertise:Expertise{deleted:false,published:true}) WHERE  (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp()) " +
            "return expertise")
    List<Expertise> getAllExpertiseByCountry(long countryId);

    @Query("MATCH (country:Country) where id(country)={0} MATCH (country)<-[:BELONGS_TO]-(expertise:Expertise{deleted:false,published:true}) return expertise LIMIT 1")
    Expertise getOneDefaultExpertiseByCountry(long countryId);

    @Query("MATCH (country:Country) where id(country)={0} MATCH (country)<-[:BELONGS_TO]-(expertise:Expertise{deleted:false,published:true}) WHERE  (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp())  with expertise, country \n" +
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


    @Query("MATCH (org:Organization) - [:" + BELONGS_TO + "] -> (country)<-[:"+BELONGS_TO+"]-(expertise:Expertise{deleted:false,published:true}) WHERE  (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp()) AND id(org)={0} return expertise")
    List<Expertise> getAllExpertiseByOrganizationId(long unitId);


    @Query("match (e:Expertise{deleted:false,published:true})-[:" + BELONGS_TO + "]->(country:Country) where id(country) = {0} AND id(e) = {1} return e")
    Expertise getExpertiesOfCountry(Long countryId, Long expertiseId);


    @Query("Match (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,hasDraftCopy:false}) where id(country) = {0}" +
            "Match(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level)\n" +
            "Match(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)\n" +
            "Match(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService)\n" +
            "with expertise,union,level, Collect(orgService) as services \n" +
            "Match(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)" +
            "Optional match(seniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable) \n" +
            "with expertise,union,level,services, Collect(payTable) as payTables, CASE when seniorityLevel IS NULL THEN [] ELSE collect({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage," +
            "   freeChoicePercentage:seniorityLevel.freeChoicePercentage,freeChoiceToPension:seniorityLevel.freeChoiceToPension, " +
            "   to:seniorityLevel.to,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels\n" +
            "OPTIONAL MATCH(expertise)-[:" + HAS_SENIOR_DAYS + "]->(seniorDays:CareDays) \n " +
            "OPTIONAL MATCH(expertise)-[:" + HAS_CHILD_CARE_DAYS + "]->(childCareDays:CareDays) \n" +
            "with expertise,payTables,union,level,services,seniorityLevels, " +
            "CASE when seniorDays IS NULL THEN [] ELSE collect(DISTINCT {id:id(seniorDays),from:seniorDays.from,to:seniorDays.to,leavesAllowed:seniorDays.leavesAllowed}) END as seniorDays, " +
            "CASE when childCareDays IS NULL THEN [] ELSE collect(DISTINCT {id:id(childCareDays),from:childCareDays.from,to:childCareDays.to,leavesAllowed:childCareDays.leavesAllowed}) END as childCareDays " +
            "return expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate, expertise.startDateMillis as startDateMillis , expertise.history as history," +
            "expertise.endDateMillis as endDateMillis ,expertise.breakPaymentSetting as breakPaymentSetting,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek,expertise.description as description ,expertise.published as published," +
            "services as organizationService,level as organizationLevel,payTables[0] as payTable,union as union,seniorityLevels,seniorDays,childCareDays order by expertise.creationDate")
    List<ExpertiseQueryResult> getUnpublishedExpertise(long countryId);


    @Query("MATCH (expertise:Expertise)-[rel:" + VERSION_OF + "]-(parentExpertise:Expertise) where id(expertise)={0} \n" +
            " set expertise.endDateMillis={1} set expertise.hasDraftCopy=false set expertise.published=true set expertise.history=true ")
    void setEndDateToExpertise(Long expertiseId, Long endDateMillis);


    @Query("match (e:Expertise)-[:" + VERSION_OF + "]->(expertise:Expertise) where id(e) = {0}" +
            "match(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level)\n" +
            "match(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)\n" +
            "match(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService)\n" +
            "with expertise,union,level, Collect(orgService) as services \n" +
            "match(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable) " +
            "return expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate, expertise.startDateMillis as startDateMillis, expertise.history as history, " +
            "expertise.endDateMillis as endDateMillis ,expertise.breakPaymentSetting as breakPaymentSetting,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek,expertise.description as description ,expertise.published as published," +
            "services as organizationService,level as organizationLevel,payTable as payTable,union as union,"
            + " CASE when seniorityLevel IS NULL THEN [] ELSE collect({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels order by expertise.creationDate")
    ExpertiseQueryResult getParentExpertiseByExpertiseId(Long expertiseId);


    @Query("match (expertise:Expertise{deleted:false}) where id(expertise) IN {0} \n" +
            "return id(expertise) as id,expertise.name as name,expertise.description as description")
    List<Expertise> getExpertiseByIdsIn(List<Long> ids);

    @Query("match (expertise:Expertise{deleted:false}) where id(expertise) = {0}" +
            "match(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level)\n" +
            "match(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)\n" +
            "match(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService)\n" +
            "with expertise,union,level, Collect(orgService) as services \n" +
            "match(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable) " +
            "return expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate, expertise.startDateMillis as startDateMillis ,expertise.history as history," +
            "expertise.endDateMillis as endDateMillis ,expertise.breakPaymentSetting as breakPaymentSetting,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek,expertise.description as description ,expertise.published as published," +
            "services as organizationService,level as organizationLevel,payTable as payTable,union as union,"
            + " CASE when seniorityLevel IS NULL THEN [] ELSE collect({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels order by expertise.creationDate")
    ExpertiseQueryResult getExpertiseById(Long expertiseId);

    @Query("match (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,published:true}) where id(country) = {0} AND (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp())" +
            "match(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level)\n" +
            "match(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)\n" +
            "match(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService)\n" +
            "with expertise,union,level, Collect(orgService) as services \n" +
            "match(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable)" +
            "OPTIONAL MATCH(expertise)-[:" + HAS_SENIOR_DAYS + "]->(seniorDays:CareDays) \n " +
            "OPTIONAL MATCH(expertise)-[:" + HAS_CHILD_CARE_DAYS + "]->(childCareDays:CareDays) \n" +
            "with expertise,payTable,union,level,services,seniorityLevel,payGradeData," +
            "CASE when seniorDays IS NULL THEN [] ELSE collect(DISTINCT {id:id(seniorDays),from:seniorDays.from,to:seniorDays.to,leavesAllowed:seniorDays.leavesAllowed}) END as seniorDays, " +
            "CASE when childCareDays IS NULL THEN [] ELSE collect(DISTINCT {id:id(childCareDays),from:childCareDays.from,to:childCareDays.to,leavesAllowed:childCareDays.leavesAllowed}) END as childCareDays ORDER BY  seniorityLevel.from \n" +
            "return expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate, expertise.startDateMillis as startDateMillis , expertise.history as history," +
            "expertise.endDateMillis as endDateMillis ,expertise.breakPaymentSetting as breakPaymentSetting,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek,expertise.description as description ,expertise.published as published," +
            "services as organizationService,level as organizationLevel,payTable as payTable,union as union,"
            + " CASE when seniorityLevel IS NULL THEN [] ELSE collect({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels ,seniorDays,childCareDays order by expertise.creationDate")
    List<ExpertiseQueryResult> getAllExpertiseByCountryId(Long countryId);


    @Query("MATCH (expertise:Expertise) where id(expertise)={0} \n" +
            " set expertise.hasDraftCopy=false set expertise.published=true set expertise.history=false ")
    void unlinkExpertiseAndMakeEditable(Long expertiseId, boolean history, boolean hasDraftCopy);

    @Query("match(expertise:Expertise{deleted:false,history:false})-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level) where id(level)={0} AND expertise.name={1}  AND id(expertise)<> {2}" +
            "with count(expertise) as expertiseCount " +
            "RETURN case when expertiseCount>0 THEN  true ELSE false END as response")
    Boolean checkExpertiseNameUniqueInOrganizationLevel(Long organizationLevelId, String expertiseName, Long currentExpertise);


    @Query("match (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,published:true}) where id(country) = {0} AND (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp()) \n" +
            "match(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService) where id(orgService) IN {1}\n" +
            " match(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(l:Level) where id(l) = {2}\n" +
            "return expertise order by expertise.creationDate")
    List<Expertise> getExpertiseByCountryAndOrganizationServices(Long countryId, List<Long> organizationServicesIds, Long organizationLevelId);


    @Query("match (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,published:true}) where id(country) = {0} AND (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp())\n" +
            "match(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService) where id(orgService) IN {1}\n" +
            "return expertise order by expertise.creationDate")
    List<Expertise> getExpertiseByCountryAndOrganizationServices(Long countryId, List<Long> organizationServicesIds);



    @Query("MATCH (o:Organization)-[r:" + PROVIDE_SERVICE + "{isEnabled:true}]->(os:OrganizationService{isEnabled:true}) WHERE id(o)={0}\n" +
            " MATCH (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,published:true}) WHERE id(country) = {1}\n" +
            " MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]->(os) return toString(id(expertise)) as id, expertise.name as value ," +
            "expertise.startDateMillis as startDateMillis ,expertise.endDateMillis as endDateMillis ORDER BY startDateMillis")
    List<FilterSelectionQueryResult> getExpertiseByCountryIdForFilters(Long unitId, Long countryId);


    @Query("match(organizationType:OrganizationType) where id(organizationType)={1}\n" +
            "match(organizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]-(os:OrganizationService)\n" +
            " match(os)<-[:SUPPORTS_SERVICES]-(expertise:Expertise{deleted:false}) where expertise.published AND  (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp())\n" +
            "return distinct id(expertise) as id,expertise.name as name")
    List<ExpertiseDTO> getExpertiseByOrganizationSubType(Long countryId, Long organizationSubTypeId);

    @Query("MATCH (country:Country) where id(country)={0}  " +
            "MATCH (country)<-[:BELONGS_TO]-(expertise:Expertise{deleted:false,published:true}) where  (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp()) " +
            "return id(expertise) as id , expertise.name as name")
    List<ExpertiseDTO> getAllExpertiseByCountryAndDate(long countryId);

    @Query("MATCH (exp:Expertise) where id(exp)={0} \n" +
            "OPTIONAL MATCH (exp)-[:"+HAS_SENIOR_DAYS+"]->(seniorDays:CareDays)\n" +
            "OPTIONAL MATCH (exp)-[:"+HAS_CHILD_CARE_DAYS+"]->(childCareDays:CareDays)\n" +
            "RETURN CASE WHEN count(childCareDays)>0 THEN COLLECT({id:id(childCareDays), from:childCareDays.from,to:childCareDays.to,leavesAllowed:childCareDays.leavesAllowed}) ELSE [] END as childCareDays ,CASE WHEN count(seniorDays)>0 THEN COLLECT({id:id(seniorDays), from:seniorDays.from,to:seniorDays.to,leavesAllowed:seniorDays.leavesAllowed}) ELSE [] END as seniorDays")
    SeniorAndChildCareDaysQueryResult getSeniorDaysOfExpertise(Long expertiseId);

}
