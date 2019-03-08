package com.kairos.dto.user.country.day_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.enums.Day;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DayType {
    protected Long id;
    private String name;
    private int code;
    private String description;
    private String colorCode;
    private List<Day> validDays=new ArrayList<>();
    private List<CountryHolidayCalenderDTO> countryHolidayCalenderData;
    private boolean holidayType;
    private boolean isEnabled = true;
    private boolean allowTimeSettings;

    // Constructor
    public DayType() {

        //default constructor
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Day> getValidDays() {
        return validDays;
    }

    public void setValidDays(List<Day> validDays) {
        this.validDays = validDays;
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

    public List<CountryHolidayCalenderDTO> getCountryHolidayCalenderData() {
        return countryHolidayCalenderData;
    }

    public void setCountryHolidayCalenderData(List<CountryHolidayCalenderDTO> countryHolidayCalenderData) {
        this.countryHolidayCalenderData = countryHolidayCalenderData;
    }

    public void setAllowTimeSettings(boolean allowTimeSettings) {
        this.allowTimeSettings = allowTimeSettings;
    }
}
