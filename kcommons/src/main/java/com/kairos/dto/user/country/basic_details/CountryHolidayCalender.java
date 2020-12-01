package com.kairos.dto.user.country.basic_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
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
    private DayTypeDTO dayType;
    private Long startTime;
    private Long endTime;
    private String description;
}
