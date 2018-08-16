package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.default_data.EmploymentTypeDTO;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.query_wrapper.CountryHolidayCalendarQueryResult;
import com.kairos.persistence.model.agreement.cta.RuleTemplateCategoryType;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.country.RelationType;
import com.kairos.persistence.model.user.resources.Vehicle;
import com.kairos.persistence.model.user.resources.VehicleQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 16/9/16.
 */

@Repository
public interface CountryGraphRepository extends Neo4jBaseRepository<Country,Long> {

    List<Country> findAll();

    @Query("MATCH (c:Country{isEnabled:true})-[:HAS_HOLIDAY]-(ch:CountryHolidayCalender{isEnabled:true}) where id(c) = {0} return {isEnabled:ch.isEnabled,description:ch.description,startTime:ch.startTime,endTime:ch.endTime,holidayTitle:ch.holidayTitle,holidayDate:ch.holidayDate} as result")
    List<Map<String,Object>> getAllCountryHolidays(Long countryId);


    @Query("MATCH (c:Country)-[:HAS_HOLIDAY]-(ch:CountryHolidayCalender) " +
            "where id(c) = {0}   AND ch.holidayDate >={1} AND  ch.holidayDate <={2}  " +
            "AND ch.isEnabled = true WITH  ch as ch  " +
            "OPTIONAL MATCH (ch)-[:DAY_TYPE]-(dt:DayType{isEnabled:true}) " +
            "return {holidayTitle: ch.holidayTitle,holidayDate: ch.holidayDate, description:ch.description,startTime:ch.startTime,id:id(ch),endTime:ch.endTime, " +
            "dayType:dt.name, " +
            "dayTypeId:id(dt)} as result")
    List<Map<String,Object>> getAllCountryHolidaysByYear(Long countryId, Long start, Long end);


    @Query("MATCH (c:Country)-[:HAS_HOLIDAY]-(ch:CountryHolidayCalender) " +
            "where id(c) = {0}  " +
            "AND ch.isEnabled = true WITH  ch as ch  " +
            "OPTIONAL MATCH (ch)-[:DAY_TYPE]-(dt:DayType{isEnabled:true}) " +
            "return {text: ch.holidayTitle,isEnabled :ch.isEnabled  ,holidayDate: ch.holidayDate, description:ch.description,startTime:ch.startTime,id:id(ch),endTime:ch.endTime, " +
            "dayType:dt.name, allowTimeSettings:dt.allowTimeSettings, colorCode:dt.colorCode," +
            "dayTypeId:id(dt)} as result order by  ch.holidayDate asc ")
    List<Map<String,Object>> getCountryAllHolidays(Long countryId);



    //query written for meta data of general tab of task type
    @Query("Match (c:Country),(o:OrganizationType)  where  c.isEnabled = true AND o.isEnable = true  return {countries: collect( DISTINCT{id:id(c),name:c.name}),types:collect( DISTINCT {id:id(o),name:o.name})} as data")
    List<Map<String,Object>> getCountryAndOrganizationTypes();

    @Query("Match (country:Country{name:{0}}) return country")
    Country getCountryByName(String name);

    @Query("MATCH (c:Country) return c")
    List<Country> findAllCountries();

    @Query("MATCH (ot:OrganizationType)-[:BELONGS_TO]->(c:Country) WHERE id(c)= {0} return ot")
    List<OrganizationType> getOrganizationTypes(Long countryId);

    @Query("MATCH (ot:OrganizationType{isEnable:true})-[:BELONGS_TO]->(c:Country) where id(c)={0} " +
            "WITH ot as ot OPTIONAL MATCH (ot)-[:HAS_SUB_TYPE]->(ost:OrganizationType{isEnable:true}) " +
            "RETURN {children: case when ost is NULL then [] else  collect({name:ost.name,id:id(ost)})end,name:ot.name,id:id(ot)} as result ")
    List<Map<String,Object>> getAllOrganizationTypes(Long countryId);

    @Query("Match (country:Country) return {id:id(country),name:country.name} as countries")
    List<Map<String,Object>> getAllCountries();

    @Query("Match (region:Region)-[:BELONGS_TO]->(country:Country) where id(region)={0} return country")
    Country getCountryByRegion(long regionId);

    @Query("Match (organization:Organization) where id(organization)={0} with organization  Match (organization)-[:"+CONTACT_ADDRESS+"]->(contactAddress:ContactAddress)-[:MUNICIPALITY]->(municipality:Municipality)-[:"+PROVINCE+"]->(province:Province)-[:"+REGION+"]->(region:Region) with region \n" +
            "Match (region)-[:"+BELONGS_TO+"]->(country:Country) return id(country)")
    Long getCountryIdByUnitId(long unitId);

    @Query("MATCH (c:Country{isEnabled:true}) return { id:id(c),name:c.name ,code:c.code,googleCalendarCode:c.googleCalendarCode} as result")
    List<Map<String,Object>> findAllCountriesMinimum();

    List<Country> findByName(String name);

    @Query("MATCH (c:Country{isEnabled:true}) WHERE LOWER(c.name)=LOWER({0}) return c")
    Country findCountryIdByName(String name);

    @Query("MATCH (c:Country{isEnabled:true}) WHERE c.name=~ {0} return c")
    List<Country> checkDuplicateCountry(String name);


    @Query("MATCH (c:Country{isEnabled:true}) WHERE c.name=~ {0} and id(c) <> {1} return c")
    List<Country> checkDuplicateCountry(String name, Long organizationId);


    @Query("match(c:Country{isEnabled:true})-[r:"+HAS_RULE_TEMPLATE_CATEGORY+"  ]-(l:RuleTemplateCategory) Where id(c)={0} AND l.ruleTemplateCategoryType={1} AND l.name=~{2}\n" +
            "return count(r) as number;")
     int checkDuplicateRuleTemplateCategory(Long id, RuleTemplateCategoryType ruleTemplateCategoryType, String name);


    @Query("MATCH (n:Country{isEnabled:true}) RETURN collect({name:n.name, code:n.code}) as list")
    List<Map> getCountryNameAndCodeList();

    @Query("Match (subService:OrganizationService) where id(subService)={0}\n" +
            "Match (os:OrganizationService)-[:ORGANIZATION_SUB_SERVICE]->(subService)\n" +
            "Match (c:Country)-[:HAS_ORGANIZATION_SERVICES]->(os) return c limit 1")
    Country getCountryByOrganizationService(long subServiceId);

    @Query("Match (team:Team) where id(team)={0} with team  Match(team)-[:CONTACT_ADDRESS]->(contactAddress:ContactAddress)-[:ZIP_CODE]->(zipCode:ZipCode)-[:MUNICIPALITY]->(muncipality:Municipality)-[:PROVINCE]->(province:Province)-[:REGION]->(region:Region) with region \n" +
            "Match (region)-[:BELONGS_TO]->(country:Country) return id(country)")
    Long getCountryOfTeam(long teamId);

    @Query("MATCH (c:Country)-[:HAS_HOLIDAY]-(ch:CountryHolidayCalender) " +
            "where id(c) = {0}   AND ch.holidayDate >={1} AND  ch.holidayDate <={2}  " +
            "AND ch.isEnabled = true WITH  ch as ch  " +
            "OPTIONAL MATCH (ch)-[:DAY_TYPE]-(dt:DayType{isEnabled:true}) " +
            "return ch.holidayDate as result")
    List<Long> getAllCountryHolidaysBetweenDates(Long countryId, Long start, Long end);

    @Query("MATCH (c:Country)-[:HAS_HOLIDAY]-(ch:CountryHolidayCalender) " +
            "where id(c) = {0}   AND ch.holidayDate >={1} AND  ch.holidayDate <={2}  " +
            "AND ch.isEnabled = true WITH  ch as ch  " +
            "OPTIONAL MATCH (ch)-[:DAY_TYPE]-(dt:DayType{isEnabled:true}) " +
            "return ch.holidayDate as result")
    List<Long> getAllCountryHolidaysCalendarsByIds(Long countryId, Long start, Long end);

    @Query("MATCH (c:Country)-[:HAS_HOLIDAY]-(ch:CountryHolidayCalender) " +
            "where id(c) = {0}   AND ch.holidayDate >={1} AND  ch.holidayDate <={2}  " +
            "AND ch.isEnabled = true WITH  ch as ch  " +
            "MATCH (ch)-[:DAY_TYPE]-(dt:DayType{isEnabled:true}) " +
            "return ch.holidayDate as holidayDate, dt as dayType ")
    List<CountryHolidayCalendarQueryResult> getCountryHolidayCalendarBetweenDates(Long countryId, Long start, Long end);



/*

    @Query("MATCH (n:Country{isEnabled:true}) where id(n)={0} with n " +
<<<<<<< HEAD
=======
            "Match (n)-[:HAS_RULE_TEMPLATE]->(t:WTABaseRuleTemplate) with t " +
            "Match (t)<-[:"+HAS_RULE_TEMPLATES+"]-(r:RuleTemplateCategory{deleted:false,ruleTemplateCategoryType:'WTA'}) with t,r " +
            "Optional MATCH(t)-[:"+HAS_BREAK_MATRIX+"]-(breakTemplateValue:BreakTemplateValue)" +
>>>>>>> KP-3090
            "Return id(t) as id ,"+
            "t.timeLimit as timeLimit,"+
            "t.balanceType as balanceType,"+
            "t.checkAgainstTimeRules as checkAgainstTimeRules,"+
            "t.minimumRest as minimumRest,"+
            "t.daysWorked as daysWorked,"+
            "t.name as name ," +
            "t.templateType as templateType," +
            "r as ruleTemplateCategory," +
            "t.disabled as disabled,"+
            "t.description as description," +
            "t.daysLimit as daysLimit,"+
            "t.creationDate as creationDate,"+
            "t.lastModificationDate as lastModificationDate,"+
            "t.nightsWorked as nightsWorked,"+
            "t.intervalLength as intervalLength,"+
            "t.intervalUnit as intervalUnit,"+
            "t.validationStartDateMillis as validationStartDateMillis,"+
            "t.balanceAdjustment as balanceAdjustment,"+
            "t.useShiftTimes as useShiftTimes,"+
            "t.maximumAvgTime as maximumAvgTime,"+
            "t.maximumVetoPercentage as maximumVetoPercentage,"+
            "t.numberShiftsPerPeriod as numberShiftsPerPeriod,"+
            "t.numberOfWeeks as numberOfWeeks,"+
            "t.fromDayOfWeek as fromDayOfWeek,"+
            "t.fromTime as fromTime,"+
            "t.proportional as proportional,"+
            "t.toTime as toTime,"+
            "t.toDayOfWeek as toDayOfWeek,"+
            "t.continuousDayRestHours as continuousDayRestHours,"+
            "t.minimumDurationBetweenShifts as minimumDurationBetweenShifts,"+
            "t.continuousWeekRest as continuousWeekRest,"+
            "t.averageRest as averageRest,"+
            "t.shiftAffiliation as shiftAffiliation,"+
            "t.shiftsLimit as shiftsLimit,"+
            "t.activityCode as activityCode,"+
<<<<<<< HEAD
            "t.onlyCompositeShifts as onlyCompositeShifts")
    List<RuleTemplateCategoryDTO> getRuleTemplatesAndCategories (long countryId);*/


    @Query("MATCH (country:Country)-[:"+HAS_LEVEL+"]->(level:Level{isEnabled:true}) where id(country)={0} AND id(level)={1} return level")
    Level getLevel(long countryId, long levelId);

    @Query("MATCH (country:Country)-[:"+HAS_LEVEL+"]->(level:Level{isEnabled:true}) where id(country)={0} return level")
    List<Level> getLevelsByCountry(long countryId);

    @Query("MATCH (country:Country)-[:"+HAS_RELATION_TYPES+"]->(relationType:RelationType{enabled:true}) where id(country)={0} return relationType")
    List<RelationType> getRelationTypesByCountry(long countryId);

    @Query("MATCH (country:Country)-[:"+HAS_RELATION_TYPES+"]->(relationType:RelationType{enabled:true}) where id(country)={0} AND id(relationType)={1} return relationType")
    RelationType getRelationType(long countryId, long relationTypeId);

    @Query("MATCH (country:Country)-[:"+HAS_RESOURCES+"]->(resources:Vehicle{enabled:true}) where id(country)={0} AND id(resources)={1} return resources")
    Vehicle getResources(long countryId, long resourcesId);

    @Query("MATCH (country:Country)-[:HAS_RESOURCES]->(res:Vehicle{enabled:true}) where id(country)={0}\n" +
            "OPTIONAL MATCH (res)-[:VEHICLE_HAS_FEATURE]->(feature:Feature{deleted:false}) with  feature,res\n" +
            "return id(res) as id,res.name as name, res.icon as icon, res.description as description, CASE WHEN feature IS NULL THEN [] ELSE collect({id:id(feature) ,name: feature.name, description:feature.description}) END as features")
    List<VehicleQueryResult> getResourcesWithFeaturesByCountry(Long countryId);

    @Query("MATCH (country:Country)-[:HAS_RESOURCES]->(res:Vehicle{enabled:true}) where id(country)={0} return res")
    List<Vehicle> getResourcesByCountry(Long countryId);

    @Query("MATCH (country:Country)-[:"+HAS_EMPLOYMENT_TYPE+"]->(employmentType:EmploymentType{deleted:false}) where id(country)={0} AND id(employmentType)={1} return employmentType")
    EmploymentType getEmploymentTypeByCountryAndEmploymentType(long countryId, long employmentTypeId);

    @Query("MATCH (country:Country)-[:"+HAS_EMPLOYMENT_TYPE+"]->(employmentType:EmploymentType) where id(country)={0} AND employmentType.deleted={1} return employmentType")
    List<EmploymentType> getEmploymentTypeByCountry(long countryId, Boolean isDeleted);

    @Query("MATCH (country:Country)-[:"+HAS_LEVEL+"]->(level:Level{isEnabled:true}) where id(country)={0} AND id(level) IN {1} return level")
    List<Level> getLevelsByIdsIn(long countryId,List<Long> levelIds);

    @Query("MATCH (country:Country)-[:"+HAS_EMPLOYMENT_TYPE+"]->(employmentType:EmploymentType) where id(country)={0} AND employmentType.deleted={1} return id(employmentType) as id ,employmentType.name as name")
    List<EmploymentTypeDTO> getEmploymentTypes(long countryId, Boolean isDeleted);

    @Query("Match (u:User) WHERE id(u)={0}\n" +
            "MATCH (u)<-[:BELONGS_TO]-(s:Staff)<-[:BELONGS_TO]-(e:Employment)<-[:HAS_EMPLOYMENTS]-(o:Organization)-[:BELONGS_TO]-(c:Country) \n" +
            "MATCH (c)-[:HAS_SYSTEM_LANGUAGE]-(sl:SystemLanguage) return sl.code LIMIT 1")
    String getSystemLanguageOfUser(long userId);
}
