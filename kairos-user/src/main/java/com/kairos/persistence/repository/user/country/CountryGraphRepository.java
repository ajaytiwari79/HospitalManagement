package com.kairos.persistence.repository.user.country;


import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.country.Country;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 16/9/16.
 */

@Repository
public interface CountryGraphRepository extends GraphRepository<Country> {

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
    List<Map<String,Object>> getAllCountryHolidaysByYear( Long countryId,Long start, Long end);


    @Query("MATCH (c:Country)-[:HAS_HOLIDAY]-(ch:CountryHolidayCalender) " +
            "where id(c) = {0}  " +
            "AND ch.isEnabled = true WITH  ch as ch  " +
            "OPTIONAL MATCH (ch)-[:DAY_TYPE]-(dt:DayType{isEnabled:true}) " +
            "return {text: ch.holidayTitle,isEnabled :ch.isEnabled  ,holidayDate: ch.holidayDate, description:ch.description,startTime:ch.startTime,id:id(ch),endTime:ch.endTime, " +
            "dayType:dt.name, colorCode:dt.colorCode," +
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
    Long getCountryOfUnit(long unitId);

    @Query("MATCH (c:Country{isEnabled:true}) return { id:id(c),name:c.name ,code:c.code} as result")
    List<Map<String,Object>> findAllCountriesMinimum();

    List<Country> findByName(String name);

    @Query("MATCH (c:Country{isEnabled:true}) WHERE c.name=~ {0} return c")
    List<Country> checkDuplicateCountry(String name);

    @Query("MATCH (c:Country{isEnabled:true}) WHERE c.name=~ {0} and id(c) <> {1} return c")
    List<Country> checkDuplicateCountry(String name,Long organizationId);


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
    List<Long> getAllCountryHolidaysBetweenDates( Long countryId,Long start, Long end);
}
