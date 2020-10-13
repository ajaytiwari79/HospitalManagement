package com.kairos.persistence.repository.day_type;

import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CustomCountryHolidayCalenderRepository {

    List<CountryHolidayCalenderDTO> getCountryAllHolidays(Long countryId);

    CountryHolidayCalenderDTO getCurrentlyActiveByCountryId(Long countryId, LocalDate holidayDate, LocalTime currentTime);


}
