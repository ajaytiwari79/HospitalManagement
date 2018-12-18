package com.kairos.shiftplanning.domain.activityConstraint;

import com.kairos.enums.Day;
import org.joda.time.DateTimeConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
