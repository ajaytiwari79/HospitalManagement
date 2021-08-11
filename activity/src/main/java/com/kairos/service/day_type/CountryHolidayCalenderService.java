package com.kairos.service.day_type;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.PublicHolidayCategory;
import com.kairos.persistence.model.day_type.CountryHolidayCalender;
import com.kairos.persistence.model.day_type.DayType;
import com.kairos.persistence.repository.day_type.CountryCalenderRepo;
import com.kairos.persistence.repository.day_type.DayTypeRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.unit_settings.ProtectedDaysOffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.PublicHolidayCategory.FIXED;

@Service
public class CountryHolidayCalenderService {

    @Inject
    private CountryCalenderRepo countryCalenderRepo;
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
        return countryCalenderRepo.getAllByCountryIdAndHolidayDateBetween(countryId, startDate, endDate);
    }

    public List<CountryHolidayCalenderDTO> getAllCountryAllHolidaysByCountryId(Long countyId) {
        return countryCalenderRepo.getCountryAllHolidays(isNull(countyId) ? UserContext.getUserDetails().getCountryId() : countyId);
    }

    @CacheEvict(value = "getDayTypeWithCountryHolidayCalender",allEntries = true)
    public CountryHolidayCalenderDTO createHolidayCalenderByCountryId(Long countryId, CountryHolidayCalenderDTO countryHolidayCalenderDTO) {
        CountryHolidayCalender countryHolidayCalender = ObjectMapperUtils.copyPropertiesByMapper(countryHolidayCalenderDTO, CountryHolidayCalender.class);
        countryHolidayCalender.setCountryId(countryId);
        countryCalenderRepo.save(countryHolidayCalender);
        countryHolidayCalenderDTO.setId(countryHolidayCalender.getId());
        protectedDaysOffService.linkProtectedDaysOffSetting(Arrays.asList(countryHolidayCalenderDTO),null,countryId);
        countryHolidayCalenderDTO.setCountryId(countryId);
        createFixedHolidayForTenYears(countryHolidayCalenderDTO);
        return countryHolidayCalenderDTO;
    }

    private void createFixedHolidayForTenYears(CountryHolidayCalenderDTO countryHolidayCalenderDTO) {
        if(FIXED.equals(countryHolidayCalenderDTO.getPublicHolidayCategory())) {
            List<CountryHolidayCalender> countryHolidayCalenders = new ArrayList<>(9);
            LocalDate holidayDate = countryHolidayCalenderDTO.getHolidayDate();
            for (int i = 1; i < 10; i++) {
                CountryHolidayCalender countryHolidayCalender = ObjectMapperUtils.copyPropertiesByMapper(countryHolidayCalenderDTO, CountryHolidayCalender.class);
                countryHolidayCalender.setHolidayDate(holidayDate.plusYears(i));
                countryHolidayCalender.setId(null);
                countryHolidayCalenders.add(countryHolidayCalender);
            }
            countryCalenderRepo.saveEntities(countryHolidayCalenders);
            protectedDaysOffService.linkProtectedDaysOffSetting(ObjectMapperUtils.copyCollectionPropertiesByMapper(countryHolidayCalenders, CountryHolidayCalenderDTO.class),null,countryHolidayCalenderDTO.getCountryId());
        }
    }

    @CacheEvict(value = "getDayTypeWithCountryHolidayCalender",allEntries = true)
    public CountryHolidayCalenderDTO updateCountryCalender(CountryHolidayCalenderDTO countryHolidayCalenderDTO) {
        LOGGER.info("Data Received: " + countryHolidayCalenderDTO);
        CountryHolidayCalender countryHolidayCalender = ObjectMapperUtils.copyPropertiesByMapper(countryHolidayCalenderDTO, CountryHolidayCalender.class);
        countryCalenderRepo.save(countryHolidayCalender);
        return countryHolidayCalenderDTO;
    }

    @CacheEvict(value = "getDayTypeWithCountryHolidayCalender",allEntries = true)
    public boolean safeDeleteCountryCalender(BigInteger id) {
        CountryHolidayCalender calender = countryCalenderRepo.findOne(id);
        if (calender != null) {
            calender.setEnabled(false);
            calender.setDeleted(true);
            countryCalenderRepo.save(calender);
            deleteLinkedHoliday(calender);
            return true;
        }
        return false;
    }

    private void deleteLinkedHoliday(CountryHolidayCalender countryHolidayCalender) {
        if(FIXED.equals(countryHolidayCalender.getPublicHolidayCategory())){
            List<LocalDate> dates = new ArrayList<>();
            for(int i=1;i<10;i++){
                dates.add(countryHolidayCalender.getHolidayDate().plusYears(i));
            }
            List<CountryHolidayCalender> calenders = countryCalenderRepo.findByHolidayDates(dates);
            calenders.forEach(calender -> {
                calender.setEnabled(false);
                calender.setDeleted(true);
            });
            countryCalenderRepo.saveEntities(calenders);
        }
    }

    public boolean transferDataOfCHCInActivity(List<CountryHolidayCalenderDTO> countryHolidayCalenderDTOS){
        List<CountryHolidayCalender> countryHolidayCalenders=ObjectMapperUtils.copyCollectionPropertiesByMapper(countryHolidayCalenderDTOS,CountryHolidayCalender.class);
        countryCalenderRepo.saveEntities(countryHolidayCalenders);
        return true;

    }

    //this method execute every year in 1st jan
    @Scheduled(cron="0 5 0 1 1 ?")
    public void createFixedPublicHoliday() {
        LOGGER.info("Yearly Job Execution {}",new Date());
        LocalDate startDate = DateUtils.getCurrentLocalDate().plusYears(1);
        LocalDate endDate = startDate.plusYears(1);
        List<CountryHolidayCalender> countryHolidayCalenders = countryCalenderRepo.getPublicHolidayByCategoryAndHolidayDateBetween(FIXED.toString(), startDate, endDate);
        List<CountryHolidayCalender> newCountryHolidayCalenders = new ArrayList<>();
        for (CountryHolidayCalender countryHolidayCalender : countryHolidayCalenders) {
            CountryHolidayCalender newCountryHolidayCalender = ObjectMapperUtils.copyPropertiesByMapper(countryHolidayCalender, CountryHolidayCalender.class);
            newCountryHolidayCalender.setHolidayDate(countryHolidayCalender.getHolidayDate().plusYears(9));
            newCountryHolidayCalender.setId(null);
            newCountryHolidayCalenders.add(newCountryHolidayCalender);
        }
        if(isCollectionNotEmpty(newCountryHolidayCalenders)) {
            countryCalenderRepo.saveEntities(newCountryHolidayCalenders);
            protectedDaysOffService.linkProtectedDaysOffSetting(ObjectMapperUtils.copyCollectionPropertiesByMapper(newCountryHolidayCalenders, CountryHolidayCalenderDTO.class), null, newCountryHolidayCalenders.get(0).getCountryId());
        }
    }
}
