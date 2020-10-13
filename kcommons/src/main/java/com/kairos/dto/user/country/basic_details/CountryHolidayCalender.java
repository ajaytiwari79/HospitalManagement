package com.kairos.dto.user.country.basic_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by oodles on 16/11/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CountryHolidayCalender {

    private String holidayTitle;
    private Long holidayDate;
    private DayType dayType;
    private Long startTime;
    private Long endTime;
    private String description;
}
