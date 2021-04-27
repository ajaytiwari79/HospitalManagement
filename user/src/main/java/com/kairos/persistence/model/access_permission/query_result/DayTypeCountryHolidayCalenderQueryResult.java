package com.kairos.persistence.model.access_permission.query_result;

import com.kairos.enums.Day;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.query_wrapper.CountryHolidayCalendarQueryResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@QueryResult
@Getter
@Setter
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
    private List<CountryHolidayCalendarQueryResult> countryHolidayCalenders;

    //TODO Will remove it after production build
    private Long holidayId;
    private LocalDate publicHolidayDate;
    private boolean protectedDaysOff;
    private Long dayTypeId;
    private Long expertiseId;

}
