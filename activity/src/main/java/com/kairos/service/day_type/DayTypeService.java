package com.kairos.service.day_type;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.wta.basic_details.WTADefaultDataInfoDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.organization.SelfRosteringMetaData;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.Day;
import com.kairos.persistence.model.day_type.CountryHolidayCalender;
import com.kairos.persistence.model.day_type.DayType;
import com.kairos.persistence.repository.day_type.CountryCalenderRepo;
import com.kairos.persistence.repository.day_type.DayTypeRepository;
import com.kairos.service.activity.PlannedTimeTypeService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.time_slot.TimeSlotSetService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.commons.utils.DateUtils.getCurrentLocalDate;
import static com.kairos.commons.utils.DateUtils.getCurrentLocalTime;
import static com.kairos.commons.utils.ObjectMapperUtils.copyCollectionPropertiesByMapper;

@Service
public class DayTypeService {

    @Inject
    private DayTypeRepository dayTypeRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private CountryCalenderRepo countryCalenderRepo;
    @Inject
    private CountryHolidayCalenderService countryHolidayCalenderService;
    @Inject
    private PlannedTimeTypeService plannedTimeTypeService;
    @Inject
    private TimeSlotSetService timeSlotService;

    @CacheEvict(value = "getDayTypeWithCountryHolidayCalender",allEntries = true)
    public DayTypeDTO createDayType(DayTypeDTO dayTypeDTO, long countryId) {
        boolean dayTypeExistInCountryByNameOrCode = dayTypeRepository.existsByCountryIdAndNameOrColorCodeIgnoreCaseAndIdNotIn(countryId, dayTypeDTO.getName(), dayTypeDTO.getCode(), new BigInteger("-1"));
        if (dayTypeExistInCountryByNameOrCode) {
            exceptionService.duplicateDataException("MESSAGE_DAYTYPE_NAME_CODE_EXIST");
        }
        DayType dayType = new DayType(dayTypeDTO.getName(), dayTypeDTO.getCode(), dayTypeDTO.getDescription(), dayTypeDTO.getColorCode(), countryId, dayTypeDTO.getValidDays(), dayTypeDTO.isHolidayType(), true, dayTypeDTO.isAllowTimeSettings());
        dayTypeRepository.save(dayType);
        dayTypeDTO.setId(dayType.getId());
        return dayTypeDTO;
    }

    @CacheEvict(value = "getDayTypeWithCountryHolidayCalender",allEntries = true)
    public DayTypeDTO updateDayType(DayTypeDTO dayTypeDTO) {
        DayType dayType = dayTypeRepository.findById(dayTypeDTO.getId()).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage("MESSAGE_EXPERTISE_ID_NOTFOUND")));
        //If there's a change in DayType name or in DayType then only verify existing DayTypes
        if (!dayTypeDTO.getName().equalsIgnoreCase(dayType.getName()) || dayTypeDTO.getCode() != dayType.getCode()) {
            boolean dayTypeExistInCountryByNameOrCode = dayTypeRepository.existsByCountryIdAndNameOrColorCodeIgnoreCaseAndIdNotIn(dayType.getCountryId(), dayTypeDTO.getName(), dayTypeDTO.getCode(), dayType.getId());
            if (dayTypeExistInCountryByNameOrCode) {
                exceptionService.duplicateDataException("MESSAGE_DAYTYPE_NAME_CODE_EXIST");
            }
        }
        dayType.setName(dayTypeDTO.getName());
        dayType.setCode(dayTypeDTO.getCode());
        dayType.setColorCode(dayTypeDTO.getColorCode());
        dayType.setDescription(dayTypeDTO.getDescription());
        dayType.setAllowTimeSettings(dayTypeDTO.isAllowTimeSettings());
        dayType.setValidDays(dayTypeDTO.getValidDays());
        dayType.setHolidayType(dayTypeDTO.isHolidayType());
        dayTypeRepository.save(dayType);

        return dayTypeDTO;
    }

    @CacheEvict(value = "getDayTypeWithCountryHolidayCalender",allEntries = true)
    public boolean deleteDayType(BigInteger dayTypeId) {
        DayType dayType = dayTypeRepository.findById(dayTypeId).orElseThrow(() -> new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage("MESSAGE_EXPERTISE_ID_NOTFOUND")));
        dayType.setDeleted(true);
        dayTypeRepository.save(dayType);
        return true;
    }

    public List<DayTypeDTO> getDayTypeByDate(Long countryId, Date date) {
        Date startDate = DateUtils.getStartOfDay(date);
        Date endDate = DateUtils.getEndOfDay(date);
        CountryHolidayCalenderDTO countryHolidayCalenderDTO = countryCalenderRepo.getByCountryIdAndHolidayDateBetween(countryId, DateUtils.asLocalDate(startDate), DateUtils.asLocalDate(endDate));

        if (Optional.ofNullable(countryHolidayCalenderDTO).isPresent()) {
            List<DayTypeDTO> dayTypes = new ArrayList<>();
            dayTypes.add(dayTypeRepository.getById(countryHolidayCalenderDTO.getId()));
            dayTypes.addAll(getDayTypes(date));
            return dayTypes;
        } else {
            return getDayTypes(date);
        }

    }

    private List<DayTypeDTO> getDayTypes(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        LocalDate localDate = localDateTime.toLocalDate();
        String day = localDate.getDayOfWeek().name();
        Day dayEnum = Day.valueOf(day);
        return dayTypeRepository.findByValidDaysContains(Stream.of(dayEnum.toString()).collect(Collectors.toList()));
    }

    public List<BigInteger> getCurrentApplicableDayType(Long countryId) {
        CountryHolidayCalender countryHolidayCalenderDTO = countryCalenderRepo.findActiveByCountryId(countryId,getCurrentLocalDate(),getCurrentLocalTime());
        List<BigInteger> dayTypes=new ArrayList<>();
        Day dayEnum = Day.valueOf(LocalDate.now().getDayOfWeek().name());
        List<DayTypeDTO> dayTypeList = dayTypeRepository.findByValidDaysContains(Stream.of(dayEnum.toString()).collect(Collectors.toList()));
        if (Optional.ofNullable(countryHolidayCalenderDTO).isPresent()) {
            dayTypes.add(countryHolidayCalenderDTO.getDayTypeId());
        }
        if(CollectionUtils.isNotEmpty(dayTypeList)){
            dayTypes.addAll(dayTypeList.stream().map(DayTypeDTO::getId).collect(Collectors.toSet()));
        }
        return dayTypes;
    }

    @CacheEvict(value = "getDayTypeWithCountryHolidayCalender",allEntries = true)
    public Map<String, TranslationInfo> updateTranslation(BigInteger dayTypeId, Map<String,TranslationInfo> translations) {
        DayType dayType =dayTypeRepository.findOne(dayTypeId);
        dayType.setTranslations(translations);
        dayTypeRepository.save(dayType);
        return dayType.getTranslations();
    }

    @Cacheable(value = "getDayTypeWithCountryHolidayCalender", key = "#countryId", cacheManager = "cacheManager")
    public List<DayTypeDTO> getDayTypeWithCountryHolidayCalender(Long countryId) {
        return dayTypeRepository.findAllByCountryIdAndDeletedFalse(countryId);
    }

    public List<DayTypeDTO> getDayTypeWithCountryHolidayCalender(Set<BigInteger> dayTypeIds) {
        return dayTypeRepository.findAllByIdInAndDeletedFalse(dayTypeIds);
    }

    public WTADefaultDataInfoDTO getWtaTemplateDefaultDataInfo(Long unitId, boolean country) {
        Long countryId = UserContext.getUserDetails().getCountryId();
        List<PresenceTypeDTO> presenceTypeDTOS = plannedTimeTypeService.getAllPresenceTypeByCountry(countryId);
        List<DayTypeDTO> dayTypes = getDayTypeWithCountryHolidayCalender(countryId);
        List<TimeSlotDTO> timeSlotDTOS =country?timeSlotService.getDefaultTimeSlot(): timeSlotService.getShiftPlanningTimeSlotByUnit(unitId);
        return new WTADefaultDataInfoDTO(dayTypes, presenceTypeDTOS, timeSlotDTOS, countryId);
    }

    public SelfRosteringMetaData getDayTypesAndPublicHoliday(Long countryId) {
        SelfRosteringMetaData publicHolidayDayTypeWrapper = new SelfRosteringMetaData();
        publicHolidayDayTypeWrapper.setDayTypes(getDayTypeWithCountryHolidayCalender(countryId));
        publicHolidayDayTypeWrapper.setPublicHolidays(countryHolidayCalenderService.getAllCountryAllHolidaysByCountryId(countryId));
        return publicHolidayDayTypeWrapper;
    }



}
