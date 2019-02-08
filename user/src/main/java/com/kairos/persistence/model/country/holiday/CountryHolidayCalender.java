package com.kairos.persistence.model.country.holiday;
import com.kairos.config.neo4j.converter.LocalTimeConverter;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.default_data.DayType;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * CountryHolidayCalender Domain extending Base Entity
 */

@NodeEntity
public class CountryHolidayCalender extends UserBaseEntity {
    private String holidayTitle;
    private LocalDate holidayDate;
    private DayType dayType;
    @Convert(LocalTimeConverter.class)
    private LocalTime startTime;
    @Convert(LocalTimeConverter.class)
    private LocalTime endTime;
    private boolean reOccuring;
    private String description;
    private String holidayType;
    private boolean isEnabled = true;
    private String googleCalId;

    public String getGoogleCalId() {
        return googleCalId;
    }

    public void setGoogleCalId(String googleCalId) {
        this.googleCalId = googleCalId;
    }

    public CountryHolidayCalender() {
    }

    public CountryHolidayCalender(String holidayTitle, LocalDate holidayDate) {
        this.holidayDate = holidayDate;
        this.holidayTitle = holidayTitle;

    }

    public CountryHolidayCalender(String holidayTitle, LocalDate holidayDate,LocalTime startTime,LocalTime endTime) {
        this.holidayDate = holidayDate;
        this.holidayTitle = holidayTitle;
        this.startTime  = startTime;
        this.endTime = endTime;
    }


    public String getHolidayType() {
        return holidayType;
    }

    public void setHolidayType(String holidayType) {
        this.holidayType = holidayType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public DayType getDayType() {
        return dayType;
    }

    public void setDayType(DayType dayType) {
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

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getHolidayTitle() {
        return holidayTitle;
    }

    public void setHolidayTitle(String holidayTitle) {
        this.holidayTitle = holidayTitle;
    }

    public boolean isReOccuring() {
        return reOccuring;
    }

    public void setReOccuring(boolean reOccuring) {
        this.reOccuring = reOccuring;
    }

    public LocalDate getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(LocalDate holidayDate) {
        this.holidayDate = holidayDate;
    }


    public Map<String, Object> retrieveDetails() {
        Map<String,Object> map = new HashMap<>();
        map.put("holidayTitle", this.holidayTitle);
        map.put("holidayDate", this.holidayDate);
        map.put("description", this.description);
        map.put("isEnabled", this.isEnabled());
        map.put("startTime", this.startTime);
        map.put("id", this.id);
        map.put("endTime", this.endTime);
        map.put("dayType", this.dayType.getName());
        map.put("allowTimeSettings", this.dayType.isAllowTimeSettings());
        map.put("dayTypeId", this.dayType.getId());
        map.put("colorCode", this.dayType.getColorCode());
        return map;

    }
}
