package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.holiday.CountryHolidayCalender;
import com.kairos.persistence.model.query_wrapper.CountryHolidayCalendarQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.DAY_TYPE;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_HOLIDAY;

/**
 * Created by oodles on 20/9/16.
 */

@Repository
public interface CountryHolidayCalenderGraphRepository extends Neo4jBaseRepository<CountryHolidayCalender,Long> {
    List<CountryHolidayCalender> findAll();

    @Query("MATCH (c:Country)-[:HAS_HOLIDAY]->(ch:CountryHolidayCalender{isEnable:true})  WHERE ch.googleCalId={0} AND  id(c)={1} return count(ch) ")
    int checkIfHolidayExist(String id, Long countryId);

    @Query("MATCH (c:Country)-[:HAS_HOLIDAY]-(ch:CountryHolidayCalender) " + "where id(c) = {0} AND date(ch.holidayDate) >=DATE({1}) AND date(ch.holidayDate) <=DATE({2}) " + "AND ch.isEnabled = true WITH ch as ch " +
            "MATCH (ch)-[:DAY_TYPE]-(dt:DayType{isEnabled:true}) " + "return ch.holidayDate as holidayDate, dt as dayType ")
    CountryHolidayCalendarQueryResult findByIdAndHolidayDateBetween(Long countryId, String startDate, String endDate);

    @Query("MATCH (country:Country)-[:"+HAS_HOLIDAY+"]->(ch:CountryHolidayCalender{isEnabled:true})-[:"+DAY_TYPE+"]->(dt:DayType{isEnabled:true}) " +
            "WHERE id(country) = {0} AND DATE(ch.holidayDate)=DATE()  AND (NOT EXISTS(ch.startTime) OR (TIME(ch.startTime)<=TIME() AND TIME(ch.endTime)>=TIME())) " +
            "RETURN ch.holidayDate as holidayDate, dt as dayType ")
    CountryHolidayCalendarQueryResult findByCountryId(Long countryId);



}
