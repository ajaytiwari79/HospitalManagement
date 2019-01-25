package com.kairos.dto.user.country.agreement.cta.cta_response;

import com.kairos.enums.Day;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class DayTypeDTO {

    private Long id;
    @NotBlank(message = "error.DayType.name.notEmpty")
    private String name;
    private List<Day> validDays = new ArrayList<>();
    private List<CountryHolidayCalenderDTO> countryHolidayCalenderData;
    private boolean holidayType;
    private boolean allowTimeSettings = false;
    private String description;
    private String country;
    @NotNull
    private int code;
    private String colorCode;

    public DayTypeDTO() {
        //default constructor
    }

    public DayTypeDTO(Long id, String name, List<Day> validDays, List<CountryHolidayCalenderDTO> countryHolidayCalenderData, boolean holidayType, boolean allowTimeSettings) {
        this.id = id;
        this.name = name;
        this.validDays = validDays;
        this.countryHolidayCalenderData = countryHolidayCalenderData;
        this.holidayType = holidayType;
        this.allowTimeSettings = allowTimeSettings;
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
        this.countryHolidayCalenderData = countryHolidayCalenderData == null ? new ArrayList<>() : countryHolidayCalenderData;
    }

    public boolean isHolidayType() {
        return holidayType;
    }

    public void setHolidayType(boolean holidayType) {
        this.holidayType = holidayType;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
