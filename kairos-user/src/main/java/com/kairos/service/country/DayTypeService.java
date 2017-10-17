package com.kairos.service.country;

import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.CountryHolidayCalender;
import com.kairos.persistence.model.user.country.Day;
import com.kairos.persistence.model.user.country.DayType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryHolidayCalenderGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.util.FormatUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by oodles on 9/1/17.
 */
@Service
@Transactional
public class DayTypeService extends UserBaseService {

    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private CountryHolidayCalenderGraphRepository countryHolidayCalenderGraphRepository;

    public Map<String, Object> createDayType(DayType dayType, long countryId){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            dayType.setCountry(country);
            save(dayType);
            return dayType.retrieveDetails();
        }
        return null;
    }

    public List<Map<String,Object>> getAllDayTypeByCountryId(long countryId){
        List<Map<String,Object>>  data = dayTypeGraphRepository.findByCountryId(countryId);
        if (data!=null){
         return FormatUtil.formatNeoResponse(data);
        }
        return  null;
    }

    public Map<String, Object> updateDayType(DayType dayType){
        DayType currentDayType = dayTypeGraphRepository.findOne(dayType.getId());
        if (currentDayType!=null){

            currentDayType.setName(dayType.getName());
            currentDayType.setCode(dayType.getCode());
            currentDayType.setColorCode(dayType.getColorCode());
            currentDayType.setDescription(dayType.getDescription());
            save(currentDayType);
            return currentDayType.retrieveDetails();
        }
        return null;
    }

    public boolean deleteDayType(long dayTypeId){
        DayType dayType = dayTypeGraphRepository.findOne(dayTypeId);
        if (dayType!=null){
            dayType.setEnabled(false);
            save(dayType);
            return true;
        }
        return false;
    }

    /**
     * @auther anil maurya
     * @param
     * @return
     */
    public DayType getDayTypeByDate(Long countryId,Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate=calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endDate=calendar.getTime();
        Optional<CountryHolidayCalender> countryHolidayCalender=countryHolidayCalenderGraphRepository.
                findByIdAndHolidayDateBetween(startDate.getTime(),endDate.getTime(),countryId);

        if(countryHolidayCalender.isPresent()){
          return countryHolidayCalender.get().getDayType();
        }else{
            Instant instant = Instant.ofEpochMilli(date.getTime());
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            LocalDate localDate = localDateTime.toLocalDate();
            String day=localDate.getDayOfWeek().name();
            Day dayEnum=Day.valueOf(day);
            //as per requirement one day may belong to many dayTypes return any day type
            List<DayType> dayTypes=dayTypeGraphRepository.findByValidDays(dayEnum);
            return dayTypes.isEmpty()?null:dayTypes.get(0);
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
                throw new UnsupportedOperationException("invalid day");


        }
        return danishName;
    }

}
