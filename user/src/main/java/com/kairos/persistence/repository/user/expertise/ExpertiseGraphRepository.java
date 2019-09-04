package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.organization.union.Location;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseDTO;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseQueryResult;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseSkillQueryResult;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseTagDTO;
import com.kairos.persistence.model.user.filter.FilterSelectionQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 28/10/16.
 */
@Repository
public interface ExpertiseGraphRepository extends Neo4jBaseRepository<Expertise, Long> {

    @Query("MATCH (country:Country) WHERE id(country)={0}  " +
            "MATCH (country)<-[:"+BELONGS_TO+"]-(expertise:Expertise{deleted:false,published:true}) WHERE  (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp()) " +
            "RETURN expertise")
    List<Expertise> getAllExpertiseByCountry(long countryId);

    @Query("MATCH (country:Country) WHERE id(country)={0} MATCH (country)<-[:"+BELONGS_TO+"]-(expertise:Expertise{deleted:false,published:true}) RETURN expertise LIMIT 1")
    Expertise getOneDefaultExpertiseByCountry(long countryId);

    @Query("MATCH (country:Country) WHERE id(country)={0} " +
            "MATCH (country)<-[:"+BELONGS_TO+"]-(expertise:Expertise{deleted:false,published:true}) WHERE  (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp())  " +
            "WITH expertise, country \n" +
            "OPTIONAL MATCH (expertise)-[:"+HAS_TAG+"]-(tag:Tag)<-[:"+COUNTRY_HAS_TAG+"]-(country) WHERE tag.deleted=false AND tag.masterDataType='EXPERTISE' " +
            "WITH expertise,tag\n" +
            "RETURN id(expertise) as id, expertise.name as name, expertise.description as description,CASE when tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag})  END as tags")
    List<ExpertiseTagDTO> getAllExpertiseWithTagsByCountry(long countryId);

    @Override
    @Query("MATCH (expertise:Expertise{deleted:false,published:true}) RETURN expertise")
    List<Expertise> findAll();

    @Query("MATCH (expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "]->(skill:Skill) WHERE id(expertise)={0} AND id(skill)={1} RETURN count(r) as countOfRel")
    int expertiseHasAlreadySkill(long expertiseId, long skillId);

    @Query("MATCH (expertise:Expertise),(skill:Skill) WHERE id (expertise)={0} AND id(skill)={1} create (expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{creationDate:{2},lastModificationDate:{3},isEnabled:true}]->(skill) RETURN skill")
    void addSkillInExpertise(long expertiseId, long skillId, long creationDate, long lastModificationDate);

    @Query("MATCH (expertise:Expertise),(skill:Skill) WHERE id (expertise)={0} AND id(skill) = {1} MATCH (expertise)-[r:" + EXPERTISE_HAS_SKILLS + "]->(skill) set r.lastModificationDate={2},r.isEnabled=true RETURN skill")
    void updateExpertiseSkill(long expertiseId, long skillId, long lastModificationDate);

    @Query("MATCH (expertise:Expertise),(skill:Skill) WHERE id(expertise)={0} AND id(skill) IN {1} MATCH (expertise)-[r:" + EXPERTISE_HAS_SKILLS + "]->(skill) set r.isEnabled=false,r.lastModificationDate={2} RETURN r")
    void deleteExpertiseSkill(long expertiseId, List<Long> skillId, long lastModificationDate);

    @Query("MATCH (expertise:Expertise) WHERE id(expertise)={0} with expertise\n" +
            "MATCH (skillCategory:SkillCategory{isEnabled:true})-[:" + BELONGS_TO + "]->(country:Country) WHERE id(country)={1} with skillCategory,expertise,country\n" +
            "MATCH (skill:Skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory) with skill,skillCategory,expertise,country\n" +
            "OPTIONAL MATCH (skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[" + COUNTRY_HAS_TAG + "]-(country)  with skill,skillCategory,expertise, CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as tags\n" +
            "optional MATCH (expertise)-[r:" + EXPERTISE_HAS_SKILLS + "]->(skill) with collect({id:id(skill),name:skill.name,isSelected:case when r.isEnabled then true else false end, tags:tags}) as skill,skillCategory\n" +
            "RETURN collect({id:id(skillCategory),name:skillCategory.name,children:skill}) as skills")
    ExpertiseSkillQueryResult getExpertiseSkills(long expertiseId, long countryId);



    @Query("MATCH (e:Expertise{deleted:false,published:true})-[:" + BELONGS_TO + "]->(country:Country) WHERE id(country) = {0} AND id(e) = {1} RETURN e")
    Expertise getExpertiesOfCountry(Long countryId, Long expertiseId);


    @Query("MATCH (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,hasDraftCopy:false}) WHERE id(country) = {0}" +
            "OPTIONAL MATCH(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level)\n" +
            "OPTIONAL MATCH(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization) WITH country,expertise,level,union\n" +
            "OPTIONAL MATCH(expertise)-[:"+BELONGS_TO_SECTOR+"]-(sector:Sector) WITH country,expertise,level,union,sector "+
            "OPTIONAL MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService)\n" +
            "with expertise,union,level, Collect(orgService) as services,sector  \n" +
            "OPTIONAL MATCH(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel) " +
            "OPTIONAL MATCH(seniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable) \n" +
            " with expertise,union,level,services,sector,Collect(payTable) as payTables, CASE when seniorityLevel IS NULL THEN [] ELSE collect({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage," +
            "   freeChoicePercentage:seniorityLevel.freeChoicePercentage,freeChoiceToPension:seniorityLevel.freeChoiceToPension, " +
            "   to:seniorityLevel.to,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels\n" +
            "OPTIONAL MATCH(expertise)-[:" + HAS_SENIOR_DAYS + "]->(seniorDays:CareDays) \n " +
            "OPTIONAL MATCH(expertise)-[:" + HAS_CHILD_CARE_DAYS + "]->(childCareDays:CareDays) \n" +
            "with expertise,payTables,union,level,services,seniorityLevels,sector, " +
            "CASE when seniorDays IS NULL THEN [] ELSE collect(DISTINCT {id:id(seniorDays),from:seniorDays.from,to:seniorDays.to,leavesAllowed:seniorDays.leavesAllowed}) END as seniorDays, " +
            "CASE when childCareDays IS NULL THEN [] ELSE collect(DISTINCT {id:id(childCareDays),from:childCareDays.from,to:childCareDays.to,leavesAllowed:childCareDays.leavesAllowed}) END as childCareDays " +
            "RETURN expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate, expertise.startDateMillis as startDateMillis , expertise.history as history, " +
            "expertise.endDateMillis as endDateMillis ,expertise.breakPaymentSetting as breakPaymentSetting,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek,expertise.description as description ,expertise.published as published," +
            "services as organizationService,level as organizationLevel,payTables[0] as payTable,union as union,seniorityLevels,seniorDays,childCareDays,sector ORDER BY expertise.name")
    List<ExpertiseQueryResult> getUnpublishedExpertise(long countryId);


    @Query("MATCH (e:Expertise)-[:" + VERSION_OF + "]->(expertise:Expertise) WHERE id(e) = {0}" +
            "MATCH(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level)\n" +
            "MATCH(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)\n" +
            "MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService)\n" +
            "with expertise,union,level, Collect(orgService) as services \n" +
            "MATCH(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable) " +
            "RETURN expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate, expertise.startDateMillis as startDateMillis, expertise.history as history, " +
            "expertise.endDateMillis as endDateMillis ,expertise.breakPaymentSetting as breakPaymentSetting,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek,expertise.description as description ,expertise.published as published," +
            "services as organizationService,level as organizationLevel,payTable as payTable,union as union,"
            + " CASE when seniorityLevel IS NULL THEN [] ELSE collect({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels order by expertise.creationDate")
    ExpertiseQueryResult getParentExpertiseByExpertiseId(Long expertiseId);


    @Query("MATCH (expertise:Expertise{deleted:false}) WHERE id(expertise) IN {0} \n" +
            "RETURN id(expertise) as id,expertise.name as name,expertise.description as description")
    List<Expertise> getExpertiseByIdsIn(List<Long> ids);

    @Query("MATCH (expertise:Expertise{deleted:false}) WHERE id(expertise) = {0}" +
            "MATCH(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level)\n" +
            "MATCH(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)\n" +
            "MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService)\n" +
            "with expertise,union,level, Collect(orgService) as services \n" +
            "MATCH(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable) " +
            "RETURN expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate, expertise.startDateMillis as startDateMillis ,expertise.history as history," +
            "expertise.endDateMillis as endDateMillis ,expertise.breakPaymentSetting as breakPaymentSetting,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek,expertise.description as description ,expertise.published as published," +
            "services as organizationService,level as organizationLevel,payTable as payTable,union as union,"
            + " CASE when seniorityLevel IS NULL THEN [] ELSE collect({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels order by expertise.creationDate")
    ExpertiseQueryResult getExpertiseById(Long expertiseId);

    @Query("MATCH (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,published:true}) WHERE id(country) = {0} AND (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp())" +
            "MATCH(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level)\n" +
            "MATCH(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)\n" +
            "MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService)\n" +
            "with expertise,union,level, Collect(orgService) as services \n" +
            "MATCH(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable)" +
            "OPTIONAL MATCH(expertise)-[:" + HAS_SENIOR_DAYS + "]->(seniorDays:CareDays) \n " +
            "OPTIONAL MATCH(expertise)-[:" + HAS_CHILD_CARE_DAYS + "]->(childCareDays:CareDays) \n" +
            "with expertise,payTable,union,level,services,seniorityLevel,payGradeData," +
            "CASE when seniorDays IS NULL THEN [] ELSE collect(DISTINCT {id:id(seniorDays),from:seniorDays.from,to:seniorDays.to,leavesAllowed:seniorDays.leavesAllowed}) END as seniorDays, " +
            "CASE when childCareDays IS NULL THEN [] ELSE collect(DISTINCT {id:id(childCareDays),from:childCareDays.from,to:childCareDays.to,leavesAllowed:childCareDays.leavesAllowed}) END as childCareDays ORDER BY  seniorityLevel.from \n" +
            "RETURN expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate, expertise.startDateMillis as startDateMillis , expertise.history as history," +
            "expertise.endDateMillis as endDateMillis ,expertise.breakPaymentSetting as breakPaymentSetting,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek,expertise.description as description ,expertise.published as published," +
            "services as organizationService,level as organizationLevel,payTable as payTable,union as union,"
            + " CASE when seniorityLevel IS NULL THEN [] ELSE collect({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels ,seniorDays,childCareDays order by expertise.creationDate")
    List<ExpertiseQueryResult> getAllExpertiseByCountryId(Long countryId);


    @Query("MATCH (expertise:Expertise) WHERE id(expertise)={0} \n" +
            " set expertise.hasDraftCopy=false set expertise.published=true set expertise.history=false ")
    void unlinkExpertiseAndMakeEditable(Long expertiseId, boolean history, boolean hasDraftCopy);

    @Query("MATCH(expertise:Expertise{deleted:false,history:false})-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level) WHERE id(level)={0} AND expertise.name={1}  AND id(expertise)<> {2}" +
            "with count(expertise) as expertiseCount " +
            "RETURN case when expertiseCount>0 THEN  true ELSE false END as response")
    Boolean checkExpertiseNameUniqueInOrganizationLevel(Long organizationLevelId, String expertiseName, Long currentExpertise);

    @Query("MATCH(expertise:Expertise{deleted:false,history:false}) where expertise.name=~{0} with count(expertise) as expertiseCount " +
            "RETURN case when expertiseCount>0 THEN  true ELSE false END as response")
    Boolean findExpertiseByUniqueName(String expertiseName);

    @Query("MATCH (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,published:true}) WHERE id(country) = {0} AND (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp()) \n" +
            "MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService) WHERE id(orgService) IN {1}\n" +
            " MATCH(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(l:Level) WHERE id(l) = {2}\n" +
            "RETURN expertise order by expertise.creationDate")
    List<Expertise> getExpertiseByCountryAndOrganizationServices(Long countryId, List<Long> organizationServicesIds, Long organizationLevelId);

    @Query("MATCH (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,published:true}) WHERE id(country) = {0} AND (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp())\n" +
            "MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService) WHERE id(orgService) IN {1}\n" +
            "RETURN expertise order by expertise.creationDate")
    List<Expertise> getExpertiseByCountryAndOrganizationServices(Long countryId, Set<Long> organizationServicesIds);


    @Query("MATCH (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,published:true}) WHERE id(country) = {0} AND (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp()) \n" +
            "MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService) WHERE id(orgService) IN {1}\n" +
            "MATCH(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(l:Level) WHERE id(l) = {2} " +
            "MATCH(expertise)-[:"+BELONGS_TO_SECTOR+"]-(sector:Sector) \n" +
            "WITH DISTINCT(expertise) as expertise,sector " +
            "RETURN id(expertise) as id,expertise.name as name,sector as sector order by expertise.creationDate")
    List<ExpertiseQueryResult> findExpertiseByCountryAndOrganizationServices(Long countryId, List<Long> organizationServicesIds, Long organizationLevelId);


    @Query("MATCH (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,published:true}) WHERE id(country) = {0} AND (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp())\n" +
            "MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService) WHERE id(orgService) IN {1} " +
            "MATCH(expertise)-[:"+BELONGS_TO_SECTOR+"]-(sector:Sector) \n" +
            "WITH DISTINCT(expertise) as expertise,sector " +
            "RETURN id(expertise) as id,expertise.name as name,sector as sector order by expertise.creationDate")
    List<ExpertiseQueryResult> findExpertiseByCountryAndOrganizationServices(Long countryId, List<Long> organizationServicesIds);



    @Query("MATCH (o:Unit)-[r:"+PROVIDE_SERVICE +"{isEnabled:true}]->(os:OrganizationService{isEnabled:true}) WHERE id(o)={0}\n" +
            " MATCH (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,published:true}) WHERE id(country) = {1}\n" +
            " MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]->(os) RETURN toString(id(expertise)) as id, expertise.name as value ," +
            "expertise.startDateMillis as startDateMillis ,expertise.endDateMillis as endDateMillis ORDER BY startDateMillis")
    List<FilterSelectionQueryResult> getExpertiseByCountryIdForFilters(Long unitId, Long countryId);

    @Query("MATCH(organizationType:OrganizationType) WHERE id(organizationType)={1}\n" +
            "MATCH(organizationType)-[:"+ORGANIZATION_TYPE_HAS_SERVICES+"]-(os:OrganizationService)\n" +
            " MATCH(os)<-[:"+SUPPORTS_SERVICES+"]-(expertise:Expertise{deleted:false}) WHERE expertise.published AND  (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp())\n" +
            "RETURN distinct id(expertise) as id,expertise.name as name")
    List<ExpertiseDTO> getExpertiseByOrganizationSubType(Long countryId, Long organizationSubTypeId);

    @Query("MATCH (country:Country) WHERE id(country)={0}  " +
            "MATCH (country)<-[:BELONGS_TO]-(expertise:Expertise{deleted:false,published:true}) WHERE  (expertise.endDateMillis IS NULL OR expertise.endDateMillis >= timestamp()) " +
            "RETURN id(expertise) as id , expertise.name as name")
    List<ExpertiseDTO> getAllExpertiseByCountryAndDate(long countryId);

    @Query("MATCH (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,published:true}) WHERE id(country) = {0}  \n" +
            "MATCH(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level) WHERE id(level) = {2} " +
            "MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService) WHERE id(orgService) IN {1}\n" +
            "WITH expertise,level,Collect(orgService) as services \n" +
            "MATCH(expertise)-[:"+BELONGS_TO_SECTOR+"]-(sector:Sector) \n" +
            "MATCH(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)\n" +
            "MATCH(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable)" +
            "OPTIONAL MATCH(expertise)-[:" + HAS_SENIOR_DAYS + "]->(seniorDays:CareDays) \n " +
            "OPTIONAL MATCH(expertise)-[:" + HAS_CHILD_CARE_DAYS + "]->(childCareDays:CareDays) \n" +
            "with expertise,payTable,union,level,services,seniorityLevel,payGradeData, sector, " +
            "CASE WHEN seniorDays IS NULL THEN [] ELSE COLLECT(DISTINCT {id:id(seniorDays),from:seniorDays.from,to:seniorDays.to,leavesAllowed:seniorDays.leavesAllowed}) END as seniorDays, " +
            "CASE WHEN childCareDays IS NULL THEN [] ELSE COLLECT(DISTINCT {id:id(childCareDays),from:childCareDays.from,to:childCareDays.to,leavesAllowed:childCareDays.leavesAllowed}) END as childCareDays ORDER BY  seniorityLevel.from \n" +
            "RETURN expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate, expertise.startDateMillis as startDateMillis ," +
            "expertise.endDateMillis as endDateMillis ,expertise.breakPaymentSetting as breakPaymentSetting,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek," +
            "services as organizationService,level as organizationLevel,payTable as payTable,union as union,sector as sector,"
            + " CASE WHEN seniorityLevel IS NULL THEN [] ELSE COLLECT({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels ,seniorDays,childCareDays order by expertise.name")
    List<ExpertiseQueryResult> findExpertiseByOrganizationServicesAndLevelForUnit(Long countryId, List<Long> organizationServicesIds, Long organizationLevelId);

    @Query("MATCH (country:Country)<-[:" + BELONGS_TO + "]-(expertise:Expertise{deleted:false,published:true}) WHERE id(country) = {0}  \n" +
            "MATCH(expertise)-[:" + SUPPORTS_SERVICES + "]-(orgService:OrganizationService) WHERE id(orgService) IN {1}\n" +
            "WITH expertise,Collect(orgService) as services \n" +
            "MATCH(expertise)-[:" + IN_ORGANIZATION_LEVEL + "]-(level:Level) " +
            "MATCH(expertise)-[:"+BELONGS_TO_SECTOR+"]-(sector:Sector) \n" +
            "MATCH(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)\n" +
            "MATCH(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade)<-[:" + HAS_PAY_GRADE + "]-(payTable:PayTable)" +
            "OPTIONAL MATCH(expertise)-[:" + HAS_SENIOR_DAYS + "]->(seniorDays:CareDays) \n " +
            "OPTIONAL MATCH(expertise)-[:" + HAS_CHILD_CARE_DAYS + "]->(childCareDays:CareDays) \n" +
            "with expertise,payTable,union,level,services,seniorityLevel,payGradeData, sector, " +
            "CASE WHEN seniorDays IS NULL THEN [] ELSE COLLECT(DISTINCT {id:id(seniorDays),from:seniorDays.from,to:seniorDays.to,leavesAllowed:seniorDays.leavesAllowed}) END as seniorDays, " +
            "CASE WHEN childCareDays IS NULL THEN [] ELSE COLLECT(DISTINCT {id:id(childCareDays),from:childCareDays.from,to:childCareDays.to,leavesAllowed:childCareDays.leavesAllowed}) END as childCareDays ORDER BY  seniorityLevel.from \n" +
            "RETURN expertise.name as name ,id(expertise) as id,expertise.creationDate as creationDate, expertise.startDateMillis as startDateMillis ," +
            "expertise.endDateMillis as endDateMillis ,expertise.breakPaymentSetting as breakPaymentSetting,expertise.fullTimeWeeklyMinutes as fullTimeWeeklyMinutes,expertise.numberOfWorkingDaysInWeek as numberOfWorkingDaysInWeek," +
            "services as organizationService,level as organizationLevel,payTable as payTable,union as union,sector as sector,"
            + " CASE WHEN seniorityLevel IS NULL THEN [] ELSE COLLECT({id:id(seniorityLevel),from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,payGrade:{id:id(payGradeData), payGradeLevel :payGradeData.payGradeLevel}})  END  as seniorityLevels ,seniorDays,childCareDays order by expertise.name")
    List<ExpertiseQueryResult> findExpertiseByOrganizationServicesForUnit(Long countryId, Set<Long> organizationServicesIds);

    @Query("MATCH (expertise:Expertise{deleted:false}) WHERE id(expertise) = {0}" +
            "MATCH(expertise)-[:" + SUPPORTED_BY_UNION + "]-(union:Organization)-[:"+HAS_LOCATION+"]-(location:Location{deleted:false})  RETURN location as name ORDER BY location.name ASC" )
    List<Location> findAllLocationsOfUnionInExpertise(Long expertiseId);

    @Query("MATCH(expertise:Expertise{deleted:false,published:true})-[:"+SUPPORTS_SERVICES+"]->(os)<-[:"+PROVIDE_SERVICE+"{isEnabled:true}]-(unit:Unit) WHERE expertise.endDateMillis IS NULL OR expertise.endDateMillis >= TIMESTAMP()\n" +
            "RETURN id(expertise) as id,expertise.name as name, collect(id(unit)) as supportedUnitIds")
    List<ExpertiseQueryResult> findAllExpertiseWithUnitIds();
}
