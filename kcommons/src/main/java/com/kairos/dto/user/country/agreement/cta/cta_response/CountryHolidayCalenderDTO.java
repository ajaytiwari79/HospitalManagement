package com.kairos.dto.user.country.agreement.cta.cta_response;

import com.kairos.dto.user.country.day_type.DayType;

/**
 * @author pradeep
 * @date - 10/12/18
 */

public class CountryHolidayCalenderDTO {

    private String holidayTitle;
    private Long holidayDate;
    private String dayType;
    private Long startTime;
    private Long endTime;
    private String description;

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

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
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
