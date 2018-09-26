package com.kairos.dto.user.country.basic_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.country.day_type.DayType;

/**
 * Created by oodles on 16/11/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryHolidayCalender {

    private String holidayTitle;
    private Long holidayDate;
    private DayType dayType;
    private Long startTime;
    private Long endTime;
    private String description;

    public CountryHolidayCalender() {

    }

    public String getHolidayTitle() {
        return holidayTitle;
    }

    public void setHolidayTitle(String holidayTitle) {
        this.holidayTitle = holidayTitle;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
