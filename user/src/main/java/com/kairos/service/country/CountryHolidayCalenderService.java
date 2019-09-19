package com.kairos.service.country;

import com.kairos.config.env.EnvConfig;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.DayType;
import com.kairos.persistence.model.country.holiday.CountryHolidayCalender;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryHolidayCalenderGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.UserMessagesConstants.START_DATE_LESS_FROM_END_DATE;

/**
 * Created by oodles on 20/9/16.
 */
@Service
@Transactional
public class CountryHolidayCalenderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryHolidayCalenderService.class);

    List<CountryHolidayCalender> countryHolidayCalenderList;

    @Inject
    EnvConfig envConfig;
    @Inject
    private CountryHolidayCalenderGraphRepository countryHolidayCalenderGraphRepository;
    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository ;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;



    /**
     *
     * @param countryHolidayCalenderDTO
     * @return
     */
    public CountryHolidayCalenderDTO updateCountryCalender(CountryHolidayCalenderDTO countryHolidayCalenderDTO){
        LOGGER.info("Data Received: "+countryHolidayCalenderDTO);
        CountryHolidayCalender calender = countryHolidayCalenderGraphRepository.findOne(countryHolidayCalenderDTO.getId());
        DayType dayType = dayTypeGraphRepository.findOne(countryHolidayCalenderDTO.getDayTypeId());
        if (calender!=null){
            if (dayType!=null){
                if (dayType.isHolidayType() && isNotNull(countryHolidayCalenderDTO.getStartTime()) && isNotNull(countryHolidayCalenderDTO.getEndTime()) && !LocalTime.MIN.equals(countryHolidayCalenderDTO.getEndTime()) && countryHolidayCalenderDTO.getEndTime().isBefore(countryHolidayCalenderDTO.getStartTime())) {
                    exceptionService.actionNotPermittedException(START_DATE_LESS_FROM_END_DATE);
                }
                dayType.setColorCode(countryHolidayCalenderDTO.getColorCode());
                calender.setHolidayDate(countryHolidayCalenderDTO.getHolidayDate());
                calender.setHolidayTitle(countryHolidayCalenderDTO.getHolidayTitle());
                calender.setDescription(countryHolidayCalenderDTO.getDescription());
                calender.setDayType(dayType);
                calender.setStartTime(countryHolidayCalenderDTO.getStartTime());
                calender.setEndTime(countryHolidayCalenderDTO.getEndTime());
                CountryHolidayCalender calender1  =countryHolidayCalenderGraphRepository.save(calender);
                LOGGER.info("Updated title: "+calender1.getHolidayTitle()+"\n Desc: "+calender1.getDescription());
            }
            LOGGER.info("dayType not found");
        }else {
            countryHolidayCalenderDTO = null;
        }
        return countryHolidayCalenderDTO;
    }

    /**
     *
     * @param id
     */
    public void deleteCountryCalender(Long id) {
        countryHolidayCalenderGraphRepository.deleteById(id);
    }


    /**
     *
     * @param id
     */
    public boolean safeDeleteCountryCalender(Long id) {
            CountryHolidayCalender calender = countryHolidayCalenderGraphRepository.findOne(id);
        if (calender!=null){
            calender.setEnabled(false);
            countryHolidayCalenderGraphRepository.save(calender);
            return true;
        }
        return false;
    }


    /**
     *
    }
     * @return
     */

    public Map<String, Object> createHolidayCalenderByCountryId(Long countryId, CountryHolidayCalenderDTO countryHolidayCalenderDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country != null) {
            CountryHolidayCalender countryHolidayCalender = new CountryHolidayCalender();
            if (countryHolidayCalenderDTO != null) {
                DayType dayType = dayTypeGraphRepository.findOne(countryHolidayCalenderDTO.getDayTypeId());
                if(dayType!=null){
                    if (dayType.isHolidayType() && isNotNull(countryHolidayCalenderDTO.getStartTime()) && isNotNull(countryHolidayCalenderDTO.getEndTime()) && !LocalTime.MIN.equals(countryHolidayCalenderDTO.getEndTime()) && countryHolidayCalenderDTO.getEndTime().isBefore(countryHolidayCalenderDTO.getStartTime())) {
                        exceptionService.actionNotPermittedException(START_DATE_LESS_FROM_END_DATE);
                    }
                    countryHolidayCalender.setHolidayDate(countryHolidayCalenderDTO.getHolidayDate());
                    countryHolidayCalender.setDescription(countryHolidayCalenderDTO.getDescription());
                    countryHolidayCalender.setHolidayTitle(countryHolidayCalenderDTO.getHolidayTitle());
                    countryHolidayCalender.setDayType(dayType);
                    countryHolidayCalender.setStartTime(countryHolidayCalenderDTO.getStartTime());
                    countryHolidayCalender.setEndTime(countryHolidayCalenderDTO.getEndTime());

                    List<CountryHolidayCalender> calenderList = country.getCountryHolidayCalenderList() != null?
                            country.getCountryHolidayCalenderList() : new ArrayList<>();
                    calenderList.add(countryHolidayCalender);
                    country.setCountryHolidayCalenderList(calenderList);
                    countryGraphRepository.save(country);
                    return countryHolidayCalender.retrieveDetails();
                }
            }
            return null;
        }
        return null;
    }

}
