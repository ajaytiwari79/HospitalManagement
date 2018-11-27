package com.kairos.persistence.model.access_permission.query_result;

import com.kairos.enums.Day;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.DayType;
import com.kairos.persistence.model.country.holiday.CountryHolidayCalender;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

@QueryResult
public class DayTypeCountryHolidayCalenderQueryResult {
    @NotBlank(message = "error.DayType.name.notEmpty")
    private Long id;
    private String name;
    @NotNull
    int code;
    private String description;
    private String colorCode;
    private Country country;
    private List<Day> validDays=new ArrayList<>();
    private boolean holidayType;
    private boolean isEnabled = true;
    private boolean allowTimeSettings = false;
    List<CountryHolidayCalenderQueryResult> countryHolidayCalenders;


    // Constructor
    public DayTypeCountryHolidayCalenderQueryResult() {
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
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

    public boolean isAllowTimeSettings() {
        return allowTimeSettings;
    }

    public void setAllowTimeSettings(boolean allowTimeSettings) {
        this.allowTimeSettings = allowTimeSettings;
    }

    public boolean isHolidayType() {
        return holidayType;
    }

    public void setHolidayType(boolean holidayType) {
        this.holidayType = holidayType;
    }


    /*public List<CountryHolidayCalender> getCountryHolidayCalender() {
        return countryHolidayCalender;
    }

    public void setCountryHolidayCalender(List<CountryHolidayCalender> countryHolidayCalender) {
        this.countryHolidayCalender = countryHolidayCalender;
    }*/

    public List<CountryHolidayCalenderQueryResult> getCountryHolidayCalenders() {
        return countryHolidayCalenders;
    }

    public void setCountryHolidayCalenders(List<CountryHolidayCalenderQueryResult> countryHolidayCalenders) {
        this.countryHolidayCalenders = countryHolidayCalenders;
    }
}
