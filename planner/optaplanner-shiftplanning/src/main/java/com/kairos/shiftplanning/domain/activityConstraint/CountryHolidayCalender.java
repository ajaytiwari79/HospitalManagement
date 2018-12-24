package com.kairos.shiftplanning.domain.activityConstraint;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author pradeep
 * @date - 18/12/18
 */

public class CountryHolidayCalender {
    private Long id;
    private boolean reOccuring;
    private String holidayType;
    private boolean isEnabled = true;
    private String googleCalId;
    private String holidayTitle;
    private LocalDate holidayDate;
    private String dayType;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
    private Long dayTypeId;
    private String colorCode;

    public CountryHolidayCalender(LocalDate holidayDate, LocalTime startTime, LocalTime endTime) {
        this.holidayDate = holidayDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public CountryHolidayCalender() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isReOccuring() {
        return reOccuring;
    }

    public void setReOccuring(boolean reOccuring) {
        this.reOccuring = reOccuring;
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

    public String getHolidayTitle() {
        return holidayTitle;
    }

    public void setHolidayTitle(String holidayTitle) {
        this.holidayTitle = holidayTitle;
    }

    public LocalDate getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(LocalDate holidayDate) {
        this.holidayDate = holidayDate;
    }

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDayTypeId() {
        return dayTypeId;
    }

    public void setDayTypeId(Long dayTypeId) {
        this.dayTypeId = dayTypeId;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
