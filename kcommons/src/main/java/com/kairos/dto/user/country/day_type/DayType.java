package com.kairos.dto.user.country.day_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.enums.Day;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
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
}
