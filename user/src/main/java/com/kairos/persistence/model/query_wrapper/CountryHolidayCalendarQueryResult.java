package com.kairos.persistence.model.query_wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.country.DayType;
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
    private Long id;
    private String holidayTitle;
    private Long startTime;
    private Long endTime;
    private boolean reOccuring;
    private String description;
    private String holidayType;
    private boolean isEnabled = true;
    private String googleCalId;


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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHolidayTitle() {
        return holidayTitle;
    }

    public void setHolidayTitle(String holidayTitle) {
        this.holidayTitle = holidayTitle;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public boolean isReOccuring() {
        return reOccuring;
    }

    public void setReOccuring(boolean reOccuring) {
        this.reOccuring = reOccuring;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHolidayType() {
        return holidayType;
    }

    public void setHolidayType(String holidayType) {
        this.holidayType = holidayType;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getGoogleCalId() {
        return googleCalId;
    }

    public void setGoogleCalId(String googleCalId) {
        this.googleCalId = googleCalId;
    }
}
