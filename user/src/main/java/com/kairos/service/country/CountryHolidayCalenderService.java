package com.kairos.service.country;

import com.kairos.config.env.EnvConfig;
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
     * @param countryHolidayCalender
     * @return
     */
    public Map<String, Object> updateCountryCalender(Map<String,Object> countryHolidayCalender){
        logger.info("Data Received: "+countryHolidayCalender);
        Long id = Long.valueOf(String.valueOf(countryHolidayCalender.get("id")));
        CountryHolidayCalender calender = countryHolidayCalenderGraphRepository.findOne(id);
        DayType dayType = dayTypeGraphRepository.findOne(Long.valueOf(String.valueOf(countryHolidayCalender.get("dayTypeId"))));

        if (calender!=null){

            if (dayType!=null){
                dayType.setColorCode(String.valueOf(countryHolidayCalender.get("colorCode")));
                String holidayDate = (String)(countryHolidayCalender.get("holidayDate"));
                calender.setHolidayDate(DateUtil.getIsoDateInLong(holidayDate));
                //calender.setHolidayDate(Long.valueOf(String.valueOf(countryHolidayCalender.get("holidayDate"))));
                calender.setHolidayTitle((String) countryHolidayCalender.get("text"));
                calender.setDescription((String) countryHolidayCalender.get("description"));
                calender.setDayType(dayType);
                calender.setStartTime(Long.valueOf(String.valueOf(countryHolidayCalender.get("startTime"))));
                calender.setEndTime(Long.valueOf(String.valueOf(countryHolidayCalender.get("endTime"))));
                CountryHolidayCalender calender1  =countryHolidayCalenderGraphRepository.save(calender);
                logger.info("Updated title: "+calender1.getHolidayTitle()+"\n Desc: "+calender1.getDescription());
                return calender1.retrieveDetails();
            }
            logger.info("dayType not found");

        }
        return null;
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


    public Map<String, Object> createHolidayCalenderByCountryId(Long countryId, Map<String,Object> data) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country != null) {
            CountryHolidayCalender countryHolidayCalender = new CountryHolidayCalender();
            if (data != null) {
                logger.info("Data Received: "+countryHolidayCalender);
                DayType dayType = dayTypeGraphRepository.findOne(Long.valueOf(String.valueOf(data.get("dayTypeId"))));

                if(dayType!=null){

                    String holidayDate = (String)(data.get("holidayDate"));
                    countryHolidayCalender.setHolidayDate(DateUtil.getIsoDateInLong(holidayDate));
                    //countryHolidayCalender.setHolidayDate(Long.valueOf(String.valueOf(data.get("holidayDate"))));
                    countryHolidayCalender.setDescription(String.valueOf(data.get("description")));
                    countryHolidayCalender.setHolidayTitle((String) data.get("text"));
                    countryHolidayCalender.setDayType(dayType);
                    countryHolidayCalender.setStartTime(Long.valueOf(String.valueOf(data.get("startTime"))));
                    countryHolidayCalender.setEndTime(Long.valueOf(String.valueOf(data.get("endTime"))));

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
                if(countryHolidayCalender == null && DateUtil.getIsoDateInLong(dateItem.getJSONObject("start").getString("date")) > DateUtil.getCurrentDate().getTime()) {
                    countryHolidayCalender = new CountryHolidayCalender();
                    countryHolidayCalender.setHolidayDate(DateUtil.getIsoDateInLong(dateItem.getJSONObject("start").getString("date")));
                    countryHolidayCalender.setHolidayTitle(dateItem.getString("summary"));
                    countryHolidayCalender.setGoogleCalId(dateItem.getString("id"));
                    countryHolidayCalender.setLastModificationDate(DateUtil.getIsoDateWithTimezoneInLong(dateItem.getString("updated")));
                    countryHolidayCalender.setStartTime(DateUtil.getIsoDateInLong(dateItem.getJSONObject("start").getString("date")));
                    countryHolidayCalender.setEndTime(DateUtil.getIsoDateInLong(dateItem.getJSONObject("end").getString("date")));
                    countryHolidayCalenderList.add(countryHolidayCalender);
                }else if(countryHolidayCalender!=null && DateUtil.getIsoDateWithTimezoneInLong(dateItem.getString("updated")) > countryHolidayCalender.getLastModificationDate()){
                    countryHolidayCalender.setHolidayTitle(dateItem.getString("summary"));
                    countryHolidayCalender.setHolidayDate(DateUtil.getIsoDateInLong(dateItem.getJSONObject("start").getString("date")));
                    countryHolidayCalender.setGoogleCalId(dateItem.getString("id"));
                    countryHolidayCalender.setLastModificationDate(DateUtil.getIsoDateWithTimezoneInLong(dateItem.getString("updated")));
                    countryHolidayCalender.setStartTime(DateUtil.getIsoDateInLong(dateItem.getJSONObject("start").getString("date")));
                    countryHolidayCalender.setEndTime(DateUtil.getIsoDateInLong(dateItem.getJSONObject("end").getString("date")));
                }
            }
            country.setCountryHolidayCalenderList(countryHolidayCalenderList);
            countryGraphRepository.save(country);
            logger.debug(datesArray.toString());
        }
    }
    }


}
