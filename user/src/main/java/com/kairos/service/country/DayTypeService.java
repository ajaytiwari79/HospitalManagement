package com.kairos.service.country;

import com.kairos.enums.Day;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.DayType;
import com.kairos.persistence.model.query_wrapper.CountryHolidayCalendarQueryResult;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryHolidayCalenderGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
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

/**
 * Created by oodles on 9/1/17.
 */
@Service
@Transactional
public class DayTypeService{

    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private CountryHolidayCalenderGraphRepository countryHolidayCalenderGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Map<String, Object> createDayType(DayType dayType, long countryId){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            dayType.setCountry(country);
            dayTypeGraphRepository.save(dayType);
            return dayType.retrieveDetails();
        }
        return null;
    }

    public List<DayType> getAllDayTypeByCountryId(long countryId){
        List<DayType>  data = dayTypeGraphRepository.findByCountryId(countryId);
        /*if (data!=null){
         return FormatUtil.formatNeoResponse(data);
        }*/
        return  data;
    }

    public Map<String, Object> updateDayType(DayType dayType){
        DayType currentDayType = dayTypeGraphRepository.findOne(dayType.getId());
        if (currentDayType!=null){
            currentDayType.setName(dayType.getName());
            currentDayType.setCode(dayType.getCode());
            currentDayType.setColorCode(dayType.getColorCode());
            currentDayType.setDescription(dayType.getDescription());
            currentDayType.setAllowTimeSettings(dayType.isAllowTimeSettings());
            currentDayType.setValidDays(dayType.getValidDays());
            currentDayType.setHolidayType(dayType.isHolidayType());
            dayTypeGraphRepository.save(currentDayType);
            return currentDayType.retrieveDetails();
        }
        return null;
    }

    public boolean deleteDayType(long dayTypeId){
        DayType dayType = dayTypeGraphRepository.findOne(dayTypeId);
        if (dayType!=null){
            dayType.setEnabled(false);
            dayTypeGraphRepository.save(dayType);
            return true;
        }
        return false;
    }

    /**
     * @auther anil maurya
     * @param
     * @return
     */
    public List<DayType> getDayTypeByDate(Long countryId,Date date){
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate=calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endDate=calendar.getTime();
        CountryHolidayCalendarQueryResult countryHolidayCalendarQueryResult=countryHolidayCalenderGraphRepository.
                findByIdAndHolidayDateBetween(countryId,startDate.getTime(),endDate.getTime());

        if(Optional.ofNullable(countryHolidayCalendarQueryResult).isPresent()){
            List<DayType> dayTypes=new ArrayList<>();
            dayTypes.add(countryHolidayCalendarQueryResult.getDayType()) ;
          return  dayTypes;
        }else{
            Instant instant = Instant.ofEpochMilli(date.getTime());
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            LocalDate localDate = localDateTime.toLocalDate();
            String day=localDate.getDayOfWeek().name();
            Day dayEnum=Day.valueOf(day);
            List<DayType> dayTypes=dayTypeGraphRepository.findByValidDaysContains(Stream.of(dayEnum.toString()).collect(Collectors.toList()));
            return dayTypes.isEmpty()?Collections.EMPTY_LIST:dayTypes;
        }

    }

    private String getDanishNameByDay(String day) {
        String danishName="";
        switch (day) {

            case "MONDAY":
                danishName = "Hverdag";
                break;
            case "TUESDAY":
                danishName = "Hverdag";
                break;
            case "WEDNESDAY":
                danishName = "Hverdag";
                break;
            case "THURSDAY":
                danishName = "Hverdag";
                break;
            case "FRIDAY":
                danishName = "Hverdag";
                break;
            case "SATURDAY":
                danishName = "Loerdag";
                break;
            case "SUNDAY":
                danishName = "Soendag";
                break;
            default:
                exceptionService.unsupportedOperationException("message.dayType.notfound");



        }
        return danishName;
    }

    public List<DayType> getDayTypes(List<Long> dayTypeIds){
        return dayTypeGraphRepository.getDayTypes(dayTypeIds);
    }


}
