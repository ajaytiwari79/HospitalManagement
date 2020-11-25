package com.kairos.persistence.repository.day_type;

import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;

import java.util.List;

public interface CustomCountryCalenderRepo {

    List<CountryHolidayCalenderDTO> getCountryAllHolidays(Long countryId);

}
