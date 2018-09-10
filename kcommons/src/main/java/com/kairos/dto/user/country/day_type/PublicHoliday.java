package com.kairos.dto.user.country.day_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalTime;
import java.util.Date;

/**
 * @author pradeep
 * @date - 22/8/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicHoliday {

    private boolean allowTimeSettings;
    private String colorCode;
    private String dayType;
    private Long dayTypeId;
    private String description;
    private LocalTime endTime;
    private Date holidayDate;
    private Long id;
    private LocalTime startTime;
    private String text;

    public boolean isAllowTimeSettings() {
        return allowTimeSettings;
    }

    public void setAllowTimeSettings(boolean allowTimeSettings) {
        this.allowTimeSettings = allowTimeSettings;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    public Long getDayTypeId() {
        return dayTypeId;
    }

    public void setDayTypeId(Long dayTypeId) {
        this.dayTypeId = dayTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Date getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(Date holidayDate) {
        this.holidayDate = holidayDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
