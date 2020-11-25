package com.kairos.dto.user.country.agreement.cta.cta_response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author pradeep
 * @date - 10/12/18
 */
@Getter
@Setter
public class CountryHolidayCalenderDTO {

    private BigInteger id;
    private boolean reOccuring;
    private boolean holidayType;
    private boolean isEnabled = true;
    private String googleCalId;
    private String holidayTitle;
    private LocalDate holidayDate;
    private String dayType;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
    private BigInteger dayTypeId;
    private String colorCode;
    private boolean allowTimeSettings;
    private DayTypeDTO dayTypeDTO;
    private Long countryId;
}
