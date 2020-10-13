package com.kairos.service.country;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.Day;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.DayType;
import com.kairos.persistence.model.query_wrapper.CountryHolidayCalendarQueryResult;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryHolidayCalenderGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.constants.UserMessagesConstants.*;

/**
 * Created by oodles on 9/1/17.
 */
@Service
@Transactional
public class DayTypeService {

    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private CountryHolidayCalenderGraphRepository countryHolidayCalenderGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private OrganizationService organizationService;

    private final Logger LOGGER = LoggerFactory.getLogger(DayTypeService.class);

    //TODO All 9 below Integrated
//    public DayTypeDTO createDayType(DayTypeDTO dayTypeDTO, long countryId) {
//
//        Boolean dayTypeExistInCountryByNameOrCode = dayTypeGraphRepository.dayTypeExistInCountryByNameOrCode(countryId, "(?i)" + dayTypeDTO.getName(), dayTypeDTO.getCode(), -1L);
//        if (dayTypeExistInCountryByNameOrCode) {
//            exceptionService.duplicateDataException(MESSAGE_DAYTYPE_NAME_CODE_EXIST);
//        }
//        Country country = countryGraphRepository.findOne(countryId);
//        if (country == null) {
//            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
//        }
//        DayType dayType = new DayType(dayTypeDTO.getName(), dayTypeDTO.getCode(), dayTypeDTO.getDescription(), dayTypeDTO.getColorCode(), country, dayTypeDTO.getValidDays(), dayTypeDTO.isHolidayType(), true, dayTypeDTO.isAllowTimeSettings());
//        dayTypeGraphRepository.save(dayType);
//        dayTypeDTO.setId(dayType.getId());
//        return dayTypeDTO;
//    }


//    public List<DayTypeDTO> getAllDayTypeByCountryId(long countryId) {
//        List<DayType> dayTypes =dayTypeGraphRepository.findByCountryId(countryId);
//        List<DayTypeDTO> dayTypeDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(dayTypes,DayTypeDTO.class);
//        for(DayTypeDTO dayTypeDTO :dayTypeDTOS){
//            dayTypeDTO.setTranslations(TranslationUtil.getTranslatedData(dayTypeDTO.getTranslatedNames(),dayTypeDTO.getTranslatedDescriptions()));
//            dayTypeDTO.setCountryId(countryId);
//        }
//        return dayTypeDTOS;
//    }

//    public List<DayType> getAllDayTypeForUnit(long unitId) {
//        Long countryId = UserContext.getUserDetails().getCountryId();
//        return dayTypeGraphRepository.findByCountryId(countryId);
//    }

//    public DayTypeDTO updateDayType(DayTypeDTO dayTypeDTO) {
//        DayType dayType = dayTypeGraphRepository.findOne(dayTypeDTO.getId());
//        if (dayType != null) {
//            //If there's a change in DayType name or in DayType then only verify existing DayTypes
//            if (!dayTypeDTO.getName().equalsIgnoreCase(dayType.getName()) || dayTypeDTO.getCode() != dayType.getCode()) {
//                Boolean dayTypeExistInCountryByNameOrCode = dayTypeGraphRepository.dayTypeExistInCountryByNameOrCode(dayType.getCountry().getId(), "(?i)" + dayTypeDTO.getName(), dayTypeDTO.getCode(), dayType.getId());
//                if (dayTypeExistInCountryByNameOrCode) {
//                    exceptionService.duplicateDataException(MESSAGE_DAYTYPE_NAME_CODE_EXIST);
//                }
//            }
//            dayType.setName(dayTypeDTO.getName());
//            dayType.setCode(dayTypeDTO.getCode());
//            dayType.setColorCode(dayTypeDTO.getColorCode());
//            dayType.setDescription(dayTypeDTO.getDescription());
//            dayType.setAllowTimeSettings(dayTypeDTO.isAllowTimeSettings());
//            dayType.setValidDays(dayTypeDTO.getValidDays());
//            dayType.setHolidayType(dayTypeDTO.isHolidayType());
//            dayTypeGraphRepository.save(dayType);
//        } else {
//            exceptionService.dataNotFoundByIdException(MESSAGE_DAYTYPE_NOTFOUND);
//        }
//        return dayTypeDTO;
//    }

//    public boolean deleteDayType(long dayTypeId) {
//        DayType dayType = dayTypeGraphRepository.findOne(dayTypeId);
//        if (dayType != null) {
//            dayType.setEnabled(false);
//            dayTypeGraphRepository.save(dayType);
//        } else {
//            exceptionService.dataNotFoundByIdException(MESSAGE_DAYTYPE_NOTFOUND);
//        }
//        return true;
//    }

    /**
     * @param
     * @return
     * @auther anil maurya
     */
//    public List<DayType> getDayTypeByDate(Long countryId, Date date) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        Date startDate = calendar.getTime();
//        calendar.set(Calendar.HOUR_OF_DAY, 23);
//        calendar.set(Calendar.MINUTE, 59);
//        calendar.set(Calendar.SECOND, 59);
//        Date endDate = calendar.getTime();
//        CountryHolidayCalendarQueryResult countryHolidayCalendarQueryResult = countryHolidayCalenderGraphRepository.
//                findByIdAndHolidayDateBetween(countryId, DateUtils.asLocalDate(startDate).toString(), DateUtils.asLocalDate(endDate).toString());
//
//        if (Optional.ofNullable(countryHolidayCalendarQueryResult).isPresent()) {
//            List<DayType> dayTypes = new ArrayList<>();
//            dayTypes.add(countryHolidayCalendarQueryResult.getDayType());
//            dayTypes.addAll(getDayTypes(date));
//            return dayTypes;
//        } else {
//            List<DayType> dayTypes = getDayTypes(date);
//            return dayTypes;
//        }
//
//    }

//    private List<DayType> getDayTypes(Date date) {
//        Instant instant = Instant.ofEpochMilli(date.getTime());
//        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
//        LocalDate localDate = localDateTime.toLocalDate();
//        String day = localDate.getDayOfWeek().name();
//        Day dayEnum = Day.valueOf(day);
//        return dayTypeGraphRepository.findByValidDaysContains(Stream.of(dayEnum.toString()).collect(Collectors.toList()));
//    }

//    public List<DayType> getDayTypes(List<Long> dayTypeIds) {
//        return dayTypeGraphRepository.getDayTypes(dayTypeIds);
//    }


    public List<DayType> getCurrentApplicableDayType(Long countryId) {
        CountryHolidayCalendarQueryResult countryHolidayCalendarQueryResult = countryHolidayCalenderGraphRepository.findByCountryId(countryId);
        List<DayType> dayTypes=new ArrayList<>();
        Day dayEnum = Day.valueOf(LocalDate.now().getDayOfWeek().name());
        List<DayType> dayTypeList = dayTypeGraphRepository.findByValidDaysContains(Stream.of(dayEnum.toString()).collect(Collectors.toList()));
        if (Optional.ofNullable(countryHolidayCalendarQueryResult).isPresent()) {
            dayTypes.add(countryHolidayCalendarQueryResult.getDayType());
        }
        if(CollectionUtils.isNotEmpty(dayTypeList)){
            dayTypes.addAll(dayTypeList);
        }
        return dayTypes;

    }
    public Map<String, TranslationInfo> updateTranslation(Long dayTypeId, Map<String,TranslationInfo> translations) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptios = new HashMap<>();
        TranslationUtil.updateTranslationData(translations,translatedNames,translatedDescriptios);
        DayType dayType =dayTypeGraphRepository.findOne(dayTypeId);
        dayType.setTranslatedNames(translatedNames);
        dayType.setTranslatedDescriptions(translatedDescriptios);
        dayTypeGraphRepository.save(dayType);
        return dayType.getTranslatedData();
    }


}
