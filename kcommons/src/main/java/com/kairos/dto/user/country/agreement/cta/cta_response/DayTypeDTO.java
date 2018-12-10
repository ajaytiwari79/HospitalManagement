package com.kairos.dto.user.country.agreement.cta.cta_response;

import com.kairos.enums.Day;

import java.util.ArrayList;
import java.util.List;

public class DayTypeDTO {
   private Long id;
   private String name;
   private List<Day> validDays=new ArrayList<>();
    private List<CountryHolidayCalenderDTO> countryHolidayCalenderData = new ArrayList<>();
    private boolean holidayType;
    private boolean allowTimeSettings = false;
    public DayTypeDTO() {
        //default constructor

    }

    public DayTypeDTO(Long id, String name, List<Day> validDays) {
        this.id = id;
        this.name = name;
        this.validDays = validDays;
    }

    public DayTypeDTO(Long id, String name, List<Day> validDays, List<CountryHolidayCalenderDTO> countryHolidayCalenderData, boolean holidayType) {
        this.id = id;
        this.name = name;
        this.validDays = validDays;
        this.countryHolidayCalenderData = countryHolidayCalenderData;
        this.holidayType = holidayType;
    }

    public boolean isAllowTimeSettings() {
        return allowTimeSettings;
    }

    public void setAllowTimeSettings(boolean allowTimeSettings) {
        this.allowTimeSettings = allowTimeSettings;
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

    public List<CountryHolidayCalenderDTO> getCountryHolidayCalenderData() {
        return countryHolidayCalenderData;
    }

    public void setCountryHolidayCalenderData(List<CountryHolidayCalenderDTO> countryHolidayCalenderData) {
        this.countryHolidayCalenderData = countryHolidayCalenderData;
    }

    public boolean isHolidayType() {
        return holidayType;
    }

    public void setHolidayType(boolean holidayType) {
        this.holidayType = holidayType;
    }
}
