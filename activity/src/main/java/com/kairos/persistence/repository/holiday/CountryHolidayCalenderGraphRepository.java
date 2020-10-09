package com.kairos.persistence.repository.holiday;

import com.kairos.persistence.model.day_type.CountryHolidayCalender;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by oodles on 20/9/16.
 */

@Repository
public interface CountryHolidayCalenderGraphRepository extends MongoBaseRepository<CountryHolidayCalender, BigInteger> {
    List<CountryHolidayCalender> findAll();

    @Query("MATCH (c:Country)-[:HAS_HOLIDAY]->(ch:CountryHolidayCalender{isEnable:true})  WHERE ch.googleCalId={0} AND  id(c)={1} return count(ch) ")
    boolean existsByGoogleCalIdAndCountryId(String id, Long countryId);

    @Query("MATCH (c:Country)-[:HAS_HOLIDAY]-(ch:CountryHolidayCalender) " + "where id(c) = {0} AND date(ch.holidayDate) >=DATE({1}) AND date(ch.holidayDate) <=DATE({2}) " + "AND ch.isEnabled = true WITH ch as ch " +
            "MATCH (ch)-[:DAY_TYPE]-(dt:DayType{isEnabled:true}) " + "return ch.holidayDate as holidayDate, dt as dayType ")
    CountryHolidayCalendarQueryResult findByIdAndHolidayDateBetween(Long countryId, String startDate, String endDate);

    CountryHolidayCalender findByCountryId(Long countryId);


    List<CountryHolidayCalender> findAllByCountryIdAndHolidayDateBetweenStartDateAndEndDate(Long countryId, String startDate, String endDate);


}
