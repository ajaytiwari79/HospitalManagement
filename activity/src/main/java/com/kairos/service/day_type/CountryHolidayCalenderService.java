package com.kairos.service.day_type;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.day_type.CountryHolidayCalender;
import com.kairos.persistence.model.day_type.DayType;
import com.kairos.persistence.repository.day_type.CountryHolidayCalenderRepository;
import com.kairos.persistence.repository.day_type.DayTypeRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.unit_settings.ProtectedDaysOffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNull;

@Service
public class CountryHolidayCalenderService {

    @Inject
    private CountryHolidayCalenderRepository countryHolidayCalenderRepository;
    @Inject
    private DayTypeRepository dayTypeRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ProtectedDaysOffService protectedDaysOffService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryHolidayCalenderService.class);

    public List<CountryHolidayCalenderDTO> getAllCountryHolidaysByCountryIdAndYear(int year, Long countryId) {
        LocalDate startDate = LocalDate.of(1, 1, year);
        LocalDate endDate = LocalDate.of(31, 12, year);
        return countryHolidayCalenderRepository.getAllByCountryIdAndHolidayDateBetween(countryId, startDate, endDate);
    }

    public List<CountryHolidayCalenderDTO> getAllCountryAllHolidaysByCountryId(Long countyId) {
        return countryHolidayCalenderRepository.getCountryAllHolidays(isNull(countyId) ? UserContext.getUserDetails().getCountryId() : countyId);
    }

    public CountryHolidayCalenderDTO createHolidayCalenderByCountryId(Long countryId, CountryHolidayCalenderDTO countryHolidayCalenderDTO) {
        CountryHolidayCalender countryHolidayCalender = ObjectMapperUtils.copyPropertiesByMapper(countryHolidayCalenderDTO, CountryHolidayCalender.class);
        countryHolidayCalender.setCountryId(countryId);
        countryHolidayCalenderRepository.save(countryHolidayCalender);
        DayType dayType=dayTypeRepository.findOne(countryHolidayCalenderDTO.getDayTypeId());
        countryHolidayCalenderDTO.setHolidayType(dayType.isHolidayType());
        countryHolidayCalenderDTO.setId(countryHolidayCalender.getId());
        protectedDaysOffService.linkProtectedDaysOffSetting(Arrays.asList(countryHolidayCalenderDTO),null,countryId);
        return countryHolidayCalenderDTO;
    }

    public CountryHolidayCalenderDTO updateCountryCalender(CountryHolidayCalenderDTO countryHolidayCalenderDTO) {
        LOGGER.info("Data Received: " + countryHolidayCalenderDTO);
        CountryHolidayCalender calender = countryHolidayCalenderRepository.findOne(countryHolidayCalenderDTO.getId());
        calender.setHolidayDate(countryHolidayCalenderDTO.getHolidayDate());
        calender.setHolidayTitle(countryHolidayCalenderDTO.getHolidayTitle());
        calender.setDescription(countryHolidayCalenderDTO.getDescription());
        calender.setDayTypeId(countryHolidayCalenderDTO.getDayTypeId());
        calender.setStartTime(countryHolidayCalenderDTO.getStartTime());
        calender.setEndTime(countryHolidayCalenderDTO.getEndTime());
        countryHolidayCalenderRepository.save(calender);
        return countryHolidayCalenderDTO;
    }

    public boolean safeDeleteCountryCalender(BigInteger id) {
        CountryHolidayCalender calender = countryHolidayCalenderRepository.findOne(id);
        if (calender != null) {
            calender.setEnabled(false);
            countryHolidayCalenderRepository.save(calender);
            return true;
        }
        return false;
    }
}
