package com.kairos.service.day_type;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.SectorWiseDayTypeInfo;
import com.kairos.dto.user.country.experties.ExpertiseDTO;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.PublicHolidayCategory.FIXED;
import static java.util.stream.Collectors.groupingBy;

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
        Set<Long> expertiseSet = getExpertiseList(countryId, countryHolidayCalenderDTO);
        if(isCollectionNotEmpty(expertiseSet)) {
            protectedDaysOffService.linkProtectedDaysOffSetting(Arrays.asList(countryHolidayCalenderDTO), expertiseSet, countryId);
        }
        countryHolidayCalenderDTO.setCountryId(countryId);
        createFixedHolidayForTenYears(countryHolidayCalenderDTO, expertiseSet);
        return countryHolidayCalenderDTO;
    }

    private Set<Long> getExpertiseList(Long countryId, CountryHolidayCalenderDTO countryHolidayCalenderDTO) {
        List<ExpertiseDTO> expertiseDTOS = userIntegrationService.getAllExpertiseInfoByCountryId(countryId);
        Map<Long, List<ExpertiseDTO>> map = expertiseDTOS.stream().collect(groupingBy(expertiseDTO ->expertiseDTO.getSector().getId()));
        Set<Long> expertiseSet = new HashSet<>();
        if(isCollectionNotEmpty(countryHolidayCalenderDTO.getSectorWiseDayTypeInfo())){
            for (SectorWiseDayTypeInfo sectorWiseDayTypeInfo : countryHolidayCalenderDTO.getSectorWiseDayTypeInfo()) {
                if("FPH".equals(sectorWiseDayTypeInfo.getHolidayType())){
                    List<ExpertiseDTO> expertiseDTOList = map.getOrDefault(sectorWiseDayTypeInfo.getSectorId(),new ArrayList<>());
                    expertiseSet.addAll(expertiseDTOList.stream().map(ExpertiseDTO::getId).collect(Collectors.toSet()));
                }
            }
        }
        return expertiseSet;
    }

    private void createFixedHolidayForTenYears(CountryHolidayCalenderDTO countryHolidayCalenderDTO, Set<Long> expertiseSet) {
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
            if(isCollectionNotEmpty(expertiseSet)) {
                protectedDaysOffService.linkProtectedDaysOffSetting(ObjectMapperUtils.copyCollectionPropertiesByMapper(countryHolidayCalenders, CountryHolidayCalenderDTO.class), expertiseSet, countryHolidayCalenderDTO.getCountryId());
            }
        }
    }

    @CacheEvict(value = "getDayTypeWithCountryHolidayCalender",allEntries = true)
    public CountryHolidayCalenderDTO updateCountryCalender(Long countryId ,CountryHolidayCalenderDTO countryHolidayCalenderDTO) {
        LOGGER.info("Data Received: " + countryHolidayCalenderDTO);
        CountryHolidayCalender oldCountryHolidayCalender = countryCalenderRepo.findOne(countryHolidayCalenderDTO.getId());
        CountryHolidayCalender countryHolidayCalender = ObjectMapperUtils.copyPropertiesByMapper(countryHolidayCalenderDTO, CountryHolidayCalender.class);
        countryCalenderRepo.save(countryHolidayCalender);
        CountryHolidayCalenderDTO copyCountryHolidayCalenderDTO = ObjectMapperUtils.copyPropertiesByMapper(countryHolidayCalenderDTO, CountryHolidayCalenderDTO.class);
        List<SectorWiseDayTypeInfo> sectorWiseDayTypeInfoList = new ArrayList<>();
        if(isCollectionNotEmpty(oldCountryHolidayCalender.getSectorWiseDayTypeInfo()) && isCollectionNotEmpty(copyCountryHolidayCalenderDTO.getSectorWiseDayTypeInfo())) {
            List<Long> oldSectorIds = oldCountryHolidayCalender.getSectorWiseDayTypeInfo().stream().map(SectorWiseDayTypeInfo::getSectorId).collect(Collectors.toList());
            for (SectorWiseDayTypeInfo wiseDayTypeInfo : copyCountryHolidayCalenderDTO.getSectorWiseDayTypeInfo()) {
                if (!oldSectorIds.contains(wiseDayTypeInfo.getSectorId())) {
                    sectorWiseDayTypeInfoList.add(wiseDayTypeInfo);
                }
            }
        }
        copyCountryHolidayCalenderDTO.setSectorWiseDayTypeInfo(sectorWiseDayTypeInfoList);
        Set<Long> expertiseSet = getExpertiseList(countryId, copyCountryHolidayCalenderDTO);
        if(isCollectionNotEmpty(expertiseSet)) {
            protectedDaysOffService.linkProtectedDaysOffSetting(Arrays.asList(countryHolidayCalenderDTO), expertiseSet, countryId);
        }
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

    public void createFixedPublicHoliday() {
        LocalDate startDate = DateUtils.getCurrentLocalDate();
        LocalDate endDate = startDate.plusYears(1);
        List<CountryHolidayCalender> countryHolidayCalenders = countryCalenderRepo.getPublicHolidayByCategoryAndHolidayDateBetween(FIXED.toString(), startDate, endDate);
        List<CountryHolidayCalender> newCountryHolidayCalenders = new ArrayList<>();
        for (CountryHolidayCalender countryHolidayCalender : countryHolidayCalenders) {
            CountryHolidayCalender newCountryHolidayCalender = ObjectMapperUtils.copyPropertiesByMapper(countryHolidayCalender, CountryHolidayCalender.class);
            newCountryHolidayCalender.setHolidayDate(countryHolidayCalender.getHolidayDate().plusYears(9));
            newCountryHolidayCalender.setId(null);
            newCountryHolidayCalenders.add(newCountryHolidayCalender);
            CountryHolidayCalenderDTO countryHolidayCalenderDTO = ObjectMapperUtils.copyPropertiesByMapper(newCountryHolidayCalender, CountryHolidayCalenderDTO.class);
            Set<Long> expertiseSet = getExpertiseList(newCountryHolidayCalender.getCountryId(), countryHolidayCalenderDTO);
            if(isCollectionNotEmpty(expertiseSet)) {
                protectedDaysOffService.linkProtectedDaysOffSetting(Arrays.asList(countryHolidayCalenderDTO), expertiseSet, newCountryHolidayCalender.getCountryId());
            }
        }
        if(isCollectionNotEmpty(newCountryHolidayCalenders)) {
            countryCalenderRepo.saveEntities(newCountryHolidayCalenders);
        }
    }
}
