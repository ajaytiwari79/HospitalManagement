package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.query_wrapper.CountryHolidayCalendarQueryResult;
import com.kairos.persistence.model.user.country.CountryHolidayCalender;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by oodles on 20/9/16.
 */

@Repository
public interface CountryHolidayCalenderGraphRepository extends Neo4jBaseRepository<CountryHolidayCalender,Long> {
    List<CountryHolidayCalender> findAll();

    @Query("MATCH (ch:CountryHolidayCalender) WHERE id(ch) ={0}   SET ch.disabled = true return ch")
    CountryHolidayCalender safeDelete(Long id);

    @Query("MATCH (c:Country)-[:HAS_HOLIDAY]->(ch:CountryHolidayCalender{isEnable:true})  WHERE ch.googleCalId={0} AND  id(c)={1} return count(ch) ")
    int checkIfHolidayExist(String id, Long countryId);

    @Query("MATCH (c:Country)-[:HAS_HOLIDAY]->(ch:CountryHolidayCalender)  WHERE ch.googleCalId={0} AND  id(c)={1} return ch ")
    CountryHolidayCalender getExistingHoliday(String id, Long countryId);

    @Query("MATCH (c:Country)-[:HAS_HOLIDAY]-(ch:CountryHolidayCalender) " + "where id(c) = {0} AND ch.holidayDate >={1} AND ch.holidayDate <={2} " + "AND ch.isEnabled = true WITH ch as ch " +
            "MATCH (ch)-[:DAY_TYPE]-(dt:DayType{isEnabled:true}) " + "return ch.holidayDate as holidayDate, dt as dayType ")
    CountryHolidayCalendarQueryResult findByIdAndHolidayDateBetween(Long countryId, Long start, Long end);


}
