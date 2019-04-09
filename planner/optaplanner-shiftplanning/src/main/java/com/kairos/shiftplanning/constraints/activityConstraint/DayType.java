package com.kairos.shiftplanning.constraints.activityConstraint;

import com.kairos.enums.Day;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 18/12/18
 */

public class DayType {
    private Long id;
    private String name;
    private List<Day> validDays=new ArrayList<>();
    private List<CountryHolidayCalender> countryHolidayCalenders;
    private boolean holidayType;
    private boolean allowTimeSettings;

    public DayType(Long id, String name, List<Day> validDays, List<CountryHolidayCalender> countryHolidayCalenders, boolean holidayType, boolean allowTimeSettings) {
        this.id = id;
        this.name = name;
        this.validDays = validDays;
        this.countryHolidayCalenders = countryHolidayCalenders;
        this.holidayType = holidayType;
        this.allowTimeSettings = allowTimeSettings;
    }

    public DayType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Day> getValidDays() {
        return validDays;
    }

    public void setValidDays(List<Day> validDays) {
        this.validDays = validDays;
    }

    public List<CountryHolidayCalender> getCountryHolidayCalenders() {
        return countryHolidayCalenders;
    }

    public void setCountryHolidayCalenders(List<CountryHolidayCalender> countryHolidayCalenders) {
        this.countryHolidayCalenders = countryHolidayCalenders;
    }

    public boolean isHolidayType() {
        return holidayType;
    }

    public void setHolidayType(boolean holidayType) {
        this.holidayType = holidayType;
    }

    public boolean isAllowTimeSettings() {
        return allowTimeSettings;
    }

    public void setAllowTimeSettings(boolean allowTimeSettings) {
        this.allowTimeSettings = allowTimeSettings;
    }


}
