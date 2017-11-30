package com.kairos.persistence.model.query_wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.country.DayType;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by oodles on 16/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class CountryHolidayCalendarQueryResult {

    private Long holidayDate;
    private DayType dayType;

    public CountryHolidayCalendarQueryResult(){}

    public Long getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(Long holidayDate) {
        this.holidayDate = holidayDate;
    }

    public DayType getDayType() {
        return dayType;
    }

    public void setDayType(DayType dayType) {
        this.dayType = dayType;
    }
}
