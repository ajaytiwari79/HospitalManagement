package com.kairos.service.country;

import com.kairos.commons.utils.DateUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.DayType;
import com.kairos.persistence.model.country.holiday.CountryHolidayCalender;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryHolidayCalenderGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.utils.DateUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 20/9/16.
 */
@Service
@Transactional
public class CountryHolidayCalenderService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    List<CountryHolidayCalender> countryHolidayCalenderList;

    @Inject
    EnvConfig envConfig;

    @Inject
    private CountryHolidayCalenderGraphRepository countryHolidayCalenderGraphRepository;
    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository ;
    @Inject
    private CountryGraphRepository countryGraphRepository;



    /**
     *
     * @param countryHolidayCalenderDTO
     * @return
     */
    public CountryHolidayCalenderDTO updateCountryCalender(CountryHolidayCalenderDTO countryHolidayCalenderDTO){
        logger.info("Data Received: "+countryHolidayCalenderDTO);
        CountryHolidayCalender calender = countryHolidayCalenderGraphRepository.findOne(countryHolidayCalenderDTO.getId());
        DayType dayType = dayTypeGraphRepository.findOne(countryHolidayCalenderDTO.getDayTypeId());
        if (calender!=null){
            if (dayType!=null){
                dayType.setColorCode(countryHolidayCalenderDTO.getColorCode());
                calender.setHolidayDate(countryHolidayCalenderDTO.getHolidayDate());
                calender.setHolidayTitle(countryHolidayCalenderDTO.getHolidayTitle());
                calender.setDescription(countryHolidayCalenderDTO.getDescription());
                calender.setDayType(dayType);
                calender.setStartTime(countryHolidayCalenderDTO.getStartTime());
                calender.setEndTime(countryHolidayCalenderDTO.getEndTime());
                CountryHolidayCalender calender1  =countryHolidayCalenderGraphRepository.save(calender);
                logger.info("Updated title: "+calender1.getHolidayTitle()+"\n Desc: "+calender1.getDescription());
            }
            logger.info("dayType not found");

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
    public List<CountryHolidayCalender> getAllCountryCalender(){
        countryHolidayCalenderList = countryHolidayCalenderGraphRepository.findAll();
        return countryHolidayCalenderList;
    }


    public Map<String, Object> createHolidayCalenderByCountryId(Long countryId, CountryHolidayCalenderDTO countryHolidayCalenderDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country != null) {
            CountryHolidayCalender countryHolidayCalender = new CountryHolidayCalender();
            if (countryHolidayCalenderDTO != null) {
                logger.info("Data Received: "+countryHolidayCalender);
                DayType dayType = dayTypeGraphRepository.findOne(countryHolidayCalenderDTO.getDayTypeId());

                if(dayType!=null){

                    countryHolidayCalender.setHolidayDate(countryHolidayCalenderDTO.getHolidayDate());
                    //countryHolidayCalender.setHolidayDate(Long.valueOf(String.valueOf(data.get("holidayDate"))));
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


    public void updateCountryHolidayCalendar() throws URISyntaxException, ParseException {
        RestTemplate restTemplate = new RestTemplate();
        List<Country> countries = countryGraphRepository.findAllCountries();

        List<CountryHolidayCalender> countryHolidayCalenderList;
        JSONArray datesArray;
        for(Country country:countries){
            if(country.getGoogleCalendarCode()!=null){
            String calendarData = restTemplate.getForObject(new URI(envConfig.getGoogleCalendarAPIV3Url(country.getGoogleCalendarCode())),String.class);
            JSONObject jsonObject = new JSONObject(calendarData);
            datesArray = jsonObject.getJSONArray("items");

            JSONObject dateItem;
            CountryHolidayCalender countryHolidayCalender;
            countryHolidayCalenderList = country.getCountryHolidayCalenderList();
            for(int i=0;i<datesArray.length();i++){
                dateItem = datesArray.getJSONObject(i);
                countryHolidayCalender = countryHolidayCalenderGraphRepository.getExistingHoliday(dateItem.getString("id"),country.getId());
                logger.info("country holiday calendar is "+countryHolidayCalender);
                countryHolidayCalender = countryHolidayCalender!=null ? countryHolidayCalender: new CountryHolidayCalender();
                countryHolidayCalender.setHolidayDate(DateUtil.getDateFromEpoch(Long.valueOf(dateItem.getJSONObject("start").getString("date"))));
                countryHolidayCalender.setHolidayTitle(dateItem.getString("summary"));
                countryHolidayCalender.setGoogleCalId(dateItem.getString("id"));
                countryHolidayCalender.setLastModificationDate(DateUtil.getIsoDateWithTimezoneInLong(dateItem.getString("updated")));
                countryHolidayCalender.setStartTime(DateUtils.asLocalTime(Long.valueOf(dateItem.getJSONObject("start").getString("date"))));
                countryHolidayCalender.setEndTime(DateUtils.asLocalTime(Long.valueOf(dateItem.getJSONObject("end").getString("date"))));

                if(countryHolidayCalender == null && DateUtil.getIsoDateInLong(dateItem.getJSONObject("start").getString("date")) > DateUtils.getCurrentDate().getTime()) {
                    countryHolidayCalender = new CountryHolidayCalender();
                    countryHolidayCalenderList.add(countryHolidayCalender);
                }else if(countryHolidayCalender!=null && DateUtil.getIsoDateWithTimezoneInLong(dateItem.getString("updated")) > countryHolidayCalender.getLastModificationDate()){
                }

            }
            country.setCountryHolidayCalenderList(countryHolidayCalenderList);
            countryGraphRepository.save(country);
            logger.debug(datesArray.toString());
        }
    }
    }


}
