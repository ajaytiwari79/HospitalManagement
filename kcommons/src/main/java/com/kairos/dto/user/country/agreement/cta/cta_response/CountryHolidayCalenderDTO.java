package com.kairos.dto.user.country.agreement.cta.cta_response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author pradeep
 * @date - 10/12/18
 */
@Getter
@Setter
public class CountryHolidayCalenderDTO {

    private Long id;
    private boolean reOccuring;
    private String holidayType;
    private boolean isEnabled = true;
    private String googleCalId;
    private String holidayTitle;
    private LocalDate holidayDate;
    private String dayType;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
    private Long dayTypeId;
    private String colorCode;
}
