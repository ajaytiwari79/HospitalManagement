package com.kairos.persistence.repository.user.country;

import com.kairos.dto.user.country.LevelDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.EmploymentTypeDTO;
import com.kairos.persistence.model.country.default_data.RelationType;
import com.kairos.persistence.model.country.default_data.RelationTypeDTO;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.query_wrapper.CountryHolidayCalendarQueryResult;
import com.kairos.persistence.model.user.resources.Vehicle;
import com.kairos.persistence.model.user.resources.VehicleQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 16/9/16.
 */

@Repository
public interface CountryGraphRepository extends Neo4jBaseRepository<Country,Long> {

    List<Country> findAll();

    @Query("MATCH (c:Country{isEnabled:true})-[:"+HAS_HOLIDAY+"]-(ch:CountryHolidayCalender{isEnabled:true}) where id(c) = {0} " +
            "RETURN {isEnabled:ch.isEnabled,description:ch.description,startTime:ch.startTime,endTime:ch.endTime,holidayTitle:ch.holidayTitle,holidayDate:ch.holidayDate} as result")
    List<Map<String,Object>> getAllCountryHolidays(Long countryId);

    @Query("MATCH (c:Country)-[:"+HAS_HOLIDAY+"]-(ch:CountryHolidayCalender) " +
            "where id(c) = {0}   AND ch.holidayDate >={1} AND  ch.holidayDate <={2}  " +
            "AND ch.isEnabled = true WITH  ch as ch  " +
            "OPTIONAL MATCH (ch)-[:"+DAY_TYPE+"]-(dt:DayType{isEnabled:true}) " +
            "RETURN {holidayTitle: ch.holidayTitle,holidayDate: ch.holidayDate, description:ch.description,startTime:ch.startTime,id:id(ch),endTime:ch.endTime, " +
            "dayType:dt.name, " +
            "dayTypeId:id(dt)} as result")
    List<Map<String,Object>> getAllCountryHolidaysByYear(Long countryId, Long start, Long end);

    @Query("MATCH (c:Country)-[:"+HAS_HOLIDAY+"]-(ch:CountryHolidayCalender{isEnabled :true}) " +
            "where id(c) = {0}  " +
            " MATCH (ch)-[:"+DAY_TYPE+"]-(dt:DayType{isEnabled:true}) " +
            "RETURN {holidayTitle: ch.holidayTitle,isEnabled :ch.isEnabled  ,holidayDate: ch.holidayDate, description:ch.description,startTime:ch.startTime,id:id(ch),endTime:ch.endTime, " +
            "dayType:dt.name, allowTimeSettings:dt.allowTimeSettings, colorCode:dt.colorCode," +
            "dayTypeId:id(dt)} as result order by  ch.holidayDate asc ")
    List<Map<String,Object>> getCountryAllHolidays(Long countryId);

    //query written for meta data of general tab of task type
    @Query("MATCH (c:Country),(o:OrganizationType)  where  c.isEnabled = true AND o.isEnable = true  RETURN {countries: collect( DISTINCT{id:id(c),name:c.name}),types:collect( DISTINCT {id:id(o),name:o.name})} as data")
    List<Map<String,Object>> getCountryAndOrganizationTypes();

    @Query("MATCH (country:Country{name:{0}}) RETURN country")
    Country getCountryByName(String name);

    @Query("MATCH (ot:OrganizationType)-[:"+BELONGS_TO+"]->(c:Country) WHERE id(c)= {0} RETURN ot")
    List<OrganizationType> getOrganizationTypes(Long countryId);

    @Query("MATCH (organization) where id(organization)={0} with organization  " +
            "MATCH (organization)-[:"+CONTACT_ADDRESS+"]->(contactAddress:ContactAddress)-[:"+MUNICIPALITY+"]->(municipality:Municipality)-[:"+PROVINCE+"]->(province:Province)-[:"+REGION+"]->(region:Region) with region \n" +
            "MATCH (region)-[:"+BELONGS_TO+"]->(country:Country) RETURN id(country)")
    Long getCountryIdByUnitId(Long unitId);

    @Query("MATCH (c:Country{isEnabled:true}) RETURN { id:id(c),name:c.name ,code:c.code,googleCalendarCode:c.googleCalendarCode} as result")
    List<Map<String,Object>> findAllCountriesMinimum();

    List<Country> findByName(String name);

    @Query("MATCH (c:Country{isEnabled:true}) WHERE c.name=~ {0} RETURN c")
    List<Country> checkDuplicateCountry(String name);

    @Query("MATCH (c:Country{isEnabled:true}) WHERE c.name=~ {0} and id(c) <> {1} RETURN c")
    List<Country> checkDuplicateCountry(String name, Long countryId);

    @Query("MATCH (n:Country{isEnabled:true}) RETURN collect({name:n.name, code:n.code}) as list")
    List<Map> getCountryNameAndCodeList();

    @Query("MATCH (subService:OrganizationService) where id(subService)={0}\n" +
            "MATCH (os:OrganizationService)-[:"+ ORGANIZATION_SUB_SERVICE +"]->(subService)\n" +
            "MATCH (c:Country)-[:"+ HAS_ORGANIZATION_SERVICES +"]->(os) RETURN c limit 1")
    Country getCountryByOrganizationService(Long subServiceId);

    @Query("MATCH (c:Country)-[:"+ HAS_HOLIDAY +"]-(ch:CountryHolidayCalender) " +
            "where id(c) = {0}   AND date(ch.holidayDate) >={1} AND  date(ch.holidayDate) <={2}  " +
            "AND ch.isEnabled = true WITH  ch as ch  " +
            "OPTIONAL MATCH (ch)-[:DAY_TYPE]-(dt:DayType{isEnabled:true}) " +
            "RETURN ch.holidayDate as result")
    List<LocalDate> getAllCountryHolidaysBetweenDates(Long countryId, LocalDate start, LocalDate end);

    @Query("MATCH (c:Country)-[:"+ HAS_HOLIDAY +"]-(ch:CountryHolidayCalender) " +
            "where id(c) = {0}   AND date(ch.holidayDate) >={1} AND  date(ch.holidayDate) <={2}  " +
            "AND ch.isEnabled = true WITH  ch as ch  " +
            "MATCH (ch)-[:DAY_TYPE]-(dt:DayType{isEnabled:true}) " +
            "RETURN ch.holidayDate as holidayDate, dt as dayType ")
    List<CountryHolidayCalendarQueryResult> getCountryHolidayCalendarBetweenDates(Long countryId, LocalDate start, LocalDate end);

    @Query("MATCH (country:Country)-[:"+HAS_LEVEL+"]->(level:Level{isEnabled:true}) where id(country)={0} AND id(level)={1} RETURN level")
    Level getLevel(Long countryId, Long levelId);

    @Query("MATCH (country:Country)-[:"+HAS_LEVEL+"]->(level:Level{isEnabled:true})<-[:"+IN_ORGANIZATION_LEVEL+"]-(payTable:PayTable{published:true}) \n" +
            "where id(country)={0} AND id(level)={1} \n" +
            "RETURN count(payTable) > 0")
    boolean isLinkedPayTablePublished(Long countryId, Long levelId);

    @Query("MATCH(ex:Expertise)  WHERE id(ex)={0} " +
            "OPTIONAL MATCH(ex)-[rel:"+IN_ORGANIZATION_LEVEL+"]-(l:Level)\n" +
            "with ex,rel  " +
            "MATCH(newLevel:Level) where id(newLevel)={1} \n" +
            "DETACH delete rel \n" +
            "CREATE UNIQUE(ex)-[:"+IN_ORGANIZATION_LEVEL+"]-(newLevel) ")
    void addLevel(Long expertiseLineId, Long newLevelToBeLinked);

    @Query("MATCH (country:Country)-[:"+HAS_LEVEL+"]->(level:Level{isEnabled:true}) where id(country)={0} RETURN level")
    List<Level> getLevelsByCountry(Long countryId);

    @Query("MATCH (country:Country)-[:"+ HAS_RELATION_TYPES +"]->(relationType:RelationType {enabled:true}) where id(country)={0} " +
            "RETURN relationType ")
    List<RelationTypeDTO> getRelationTypesByCountry(Long countryId);

    @Query("MATCH (country:Country)-[:"+HAS_RELATION_TYPES+"]->(relationType:RelationType{enabled:true}) where id(country)={0} AND id(relationType)={1} RETURN relationType")
    RelationType getRelationType(Long countryId, Long relationTypeId);

    @Query("MATCH(country:Country)-[:" + HAS_RELATION_TYPES + "]->(relationType:RelationType {enabled:true}) WHERE id(country)={0} AND id(relationType)<>{2} AND relationType.name =~{1}  " +
            " WITH count(relationType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    boolean relationTypeExistInCountryByName(Long countryId, String name, Long currentRelationTypeId);

    @Query("MATCH (country:Country)-[:"+HAS_RESOURCES+"]->(resources:Vehicle{enabled:true}) where id(country)={0} AND id(resources)={1} RETURN resources")
    Vehicle getResources(Long countryId, Long resourcesId);

    @Query("MATCH(country:Country)-[:" + HAS_RESOURCES + "]->(resources:Vehicle {enabled:true}) WHERE id(country)={0} AND id(resources)<>{2} AND resources.name =~{1}  " +
            " WITH count(resources) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean vehicleExistInCountryByName(Long countryId, String name, Long currentResourceId);

    @Query("MATCH (country:Country)-[:"+HAS_RESOURCES+"]->(res:Vehicle{enabled:true}) where id(country)={0}\n" +
            "OPTIONAL MATCH (res)-[:"+VEHICLE_HAS_FEATURE+"]->(feature:Feature{deleted:false}) with  res, \n" +
            "CASE WHEN feature IS NULL THEN [] ELSE collect({id:id(feature) ,name: feature.name, description:feature.description}) END as features \n"+
            "RETURN id(res) as id,res.name as name, res.icon as icon, res.description as description, features as features ORDER BY res.creationDate DESC")
    List<VehicleQueryResult> getResourcesWithFeaturesByCountry(Long countryId);

    @Query("MATCH (country:Country)-[:"+HAS_RESOURCES+"]->(res:Vehicle{enabled:true}) where id(country)={0} RETURN res ORDER BY res.creationDate DESC")
    List<Vehicle> getResourcesByCountry(Long countryId);

    @Query("MATCH (country:Country)-[:"+HAS_EMPLOYMENT_TYPE+"]->(employmentType:EmploymentType{deleted:false}) where id(country)={0} AND id(employmentType)={1} RETURN employmentType")
    EmploymentType getEmploymentTypeByCountryAndEmploymentType(Long countryId, Long employmentTypeId);

    @Query("MATCH (country:Country)-[:"+HAS_EMPLOYMENT_TYPE+"]->(employmentType:EmploymentType) where id(country)={0} AND employmentType.deleted={1} " +
            "RETURN employmentType")
    List<EmploymentType> getEmploymentTypeByCountry(Long countryId, Boolean isDeleted);

    @Query("MATCH (country:Country)-[:"+HAS_LEVEL+"]->(level:Level{isEnabled:true}) where id(country)={0} AND id(level) IN {1} RETURN level")
    List<Level> getLevelsByIdsIn(Long countryId,List<Long> levelIds);

    @Query("MATCH (country:Country)-[:"+HAS_EMPLOYMENT_TYPE+"]->(employmentType:EmploymentType) where id(country)={0} AND employmentType.deleted={1} " +
            "RETURN id(employmentType) as id ,employmentType.name as name ORDER BY employmentType.creationDate DESC")
    List<EmploymentTypeDTO> getEmploymentTypes(Long countryId, Boolean isDeleted);

    @Query("MATCH(country:Country{deleted:false,isEnabled:true}) where id(country)={0} RETURN country")
    Country findCountryById(Long countryId);

    @Query("MATCH (organization) where id(organization)={0} with organization  " +
            "MATCH (organization)-[:"+CONTACT_ADDRESS+"]->(contactAddress:ContactAddress)-[:"+MUNICIPALITY+"]->(municipality:Municipality)-[:"+PROVINCE+"]->(province:Province)-[:"+REGION+"]->(region:Region) with region \n" +
            "MATCH (region)-[:"+BELONGS_TO+"]->(country:Country) RETURN country")
    Country getCountryByUnitId(Long unitId);

    @Query("MATCH (ch:CountryHolidayCalender{isEnabled:true})-[:"+DAY_TYPE+"]->(dt:DayType{isEnabled:true}) " +
            "WHERE id(ch) ={0}"  +
            "RETURN ch.holidayDate as holidayDate, dt as dayType ")
    CountryHolidayCalendarQueryResult findByCalendarHolidayId(Long calendarHolidayId);



    @Query("MATCH (ch:CountryHolidayCalender{isEnabled:true})-[:"+DAY_TYPE+"]->(dt:DayType{isEnabled:true}) " +
            "RETURN ch.holidayDate as holidayDate, dt as dayType ,id(ch) as id")
    List<CountryHolidayCalendarQueryResult> findAllCalendarHoliday();

}
