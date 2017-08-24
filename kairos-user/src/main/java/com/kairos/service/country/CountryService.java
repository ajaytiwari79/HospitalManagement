package com.kairos.service.country;


import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.organization.OrganizationTypeHierarchyQueryResult;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.CountryHolidayCalender;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.CountryHolidayCalenderGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.access_permisson.AccessGroupService;
import com.kairos.service.google_calender.GoogleCalenderService;
import com.kairos.util.FormatUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by oodles on 16/9/16.
 */
@Service
@Transactional
public class CountryService extends UserBaseService {
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME =
            "Google Calendar API Java Quickstart";
    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/calendar-java-quickstart");
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();
    /**
     * Global instance of the scopes required by this quickstart.
     * <p>
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart
     */
    private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR_READONLY);
    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Inject
    private CountryHolidayCalenderGraphRepository countryHolidayGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private RegionGraphRepository regionGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private AccessGroupService accessGroupService;
    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository;
    @Autowired
    OrganizationTypeGraphRepository organizationTypeGraphRepository;

    /**
     * @param country
     * @return
     */
    public Map<String, Object> createCountry(Country country) {
        String name = "(?i)" + country.getName();
        List<Country> countryFound = countryGraphRepository.checkDuplicateCountry(name);
        if (countryFound == null || countryFound.isEmpty()) {
            super.save(country);
            return country.retrieveDetails();
        }
        throw new DuplicateDataException("Can't create duplicate name country");
    }


    /**
     * @param id
     * @return
     */
    public Country getCountryById(Long id) {
        return (Country) super.findOne(id);
    }


    /**
     * @param country
     * @return
     */
    public Map<String, Object> updateCountry(Country country) {
        String name = "(?i)" + country.getName();
        List<Country> duplicateCountryList = countryGraphRepository.checkDuplicateCountry(name,country.getId());
        if(!duplicateCountryList.isEmpty()){
            throw new DuplicateDataException("Can't create duplicate name country");
        }
        Country currentCountry = (Country) findOne(country.getId());
        if(country == null){
            throw new InternalError("Country not found");
        }
        currentCountry.setName(country.getName());
        currentCountry.setCode(country.getCode());
        currentCountry.setGoogleCalendarCode(country.getGoogleCalendarCode());
        save(currentCountry);
        return currentCountry.retrieveDetails();
    }


    /**
     * @param id
     */
    public boolean deleteCountry(Long id) {
        Country currentCountry = (Country) findOne(id);
        if (currentCountry != null) {
            currentCountry.setEnabled(false);
            save(currentCountry);
            return true;
        }
        return false;
    }


    /**
     * @return
     */
    public List<Map<String,Object>> getAllCountries() {
        return FormatUtil.formatNeoResponse(countryGraphRepository.findAllCountriesMinimum());
    }


    public List<Object> getAllCountryHolidaysByCountryIdAndYear(int year, Long countryId) {
        Long start = new DateTime().withYear(year).withDayOfYear(1).getMillis();
        List<Object> objectList = new ArrayList<>();
        Long end;
        GregorianCalendar cal = new GregorianCalendar();
        if (cal.isLeapYear(year)) {
            logger.info("Leap Year found");
            end = new DateTime().withYear(year).withDayOfYear(366).getMillis();
        } else {
            logger.info("No Leap Year found");
            end = new DateTime().withYear(year).withDayOfYear(365).getMillis();
        }
        logger.info("Year Start: " + start + "  Year end:" + end);
        List<Map<String, Object>> data = countryGraphRepository.getAllCountryHolidaysByYear(countryId, start, end);
        if (!data.isEmpty()) {
            for (Map<String, Object> map : data) {
                Object o = map.get("result");
                objectList.add(o);
            }
        }


        return objectList;
    }


    public void fetchHolidaysFromGoogleCalender(Long countryId) {
        List<CountryHolidayCalender> calenderList = new ArrayList<>();
        Country country = countryGraphRepository.findOne(countryId, 2);
        if (country == null) {
            throw new DataNotFoundByIdException("Can't find country with provided Id");
        }
        try {
            List<Event> eventList = GoogleCalenderService.getEventsFromGoogleCalender();
            if (eventList != null) {
                logger.info("No. of Events received are: " + eventList.size());
            }
            CountryHolidayCalender holidayCalender = null;
            for (Event event : eventList) {
                logger.info("StartTime: "+event.getStart().getDateTime()+"  End: "+event.getEnd().getDateTime()+"     Visibility: "+event.getVisibility()+":"+event.getColorId()+"   Status:"+event.getStatus()+"    Kind: "+event.getKind()+event.getStart()+"  Title"+event.getSummary());

                holidayCalender = new CountryHolidayCalender();
                holidayCalender.setHolidayTitle(event.getSummary() != null ? event.getSummary() : "");
                holidayCalender.setHolidayDate(DateTime.parse(event.getStart().get("date").toString()).getMillis());
                holidayCalender.setHolidayType(event.getVisibility() != null ? event.getSummary() : "");
                holidayCalender.setGoogleCalId(event.getId());

                if (countryHolidayGraphRepository.checkIfHolidayExist(event.getId(), countryId) > 0) {
                    logger.info("Duplicate Holiday");
                } else {
                    logger.info("Unique Holiday");
                    holidayCalender.setGoogleCalId(event.getId());
                    calenderList.add(holidayCalender);
                }

                // Setting holiday to Country
                if (country.getCountryHolidayCalenderList() == null) {
                    logger.info("Adding holidays");
                    country.setCountryHolidayCalenderList(calenderList);
                } else {
                    logger.info("Adding holidays again");
                    List<CountryHolidayCalender> currentHolidayList = country.getCountryHolidayCalenderList();
                    currentHolidayList.addAll(calenderList);
                    country.setCountryHolidayCalenderList(currentHolidayList);
                }
                countryGraphRepository.save(country);


            }
        } catch (Exception e) {
            logger.info("Exception occured: " + e.getCause());
            e.printStackTrace();
        }

    }


    public List<Map<String,Object>> getAllCountryAllHolidaysByCountryId(Long countryId) {
        if (countryId == null) {
            return null;
        }
               // return stored holidays in database
        return FormatUtil.formatNeoResponse(countryGraphRepository.getCountryAllHolidays(countryId));

    }

    public boolean triggerGoogleCalenderService(Long countryId) {
        try {

            if (DateTime.now().getYear()==2017){
                logger.info("Running google service in 2017");
                fetchHolidaysFromGoogleCalender(countryId);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;


    }



    public List<OrganizationType> getOrganizationTypes(Long countryId) {
        return countryGraphRepository.getOrganizationTypes(countryId);
    }

    public Country getCountryByName(String name) {
        return countryGraphRepository.getCountryByName(name);
    }

    public List<Map> getCountryNameAndCodeList() {
        return countryGraphRepository.getCountryNameAndCodeList();
    }

    /**
     * @auther anil maurya
     * @param subServiceId
     * @param organizationSubTypes
     */
    public  Map<String, Object> getAllCountryWithOrganizationTypes(Long subServiceId,Set<Long> organizationSubTypes ){
        Map<String, Object> response = new HashMap<>();
        for (Map<String, Object> map : countryGraphRepository.getCountryAndOrganizationTypes()) {
            response.put("countries", map.get("countries"));
            response.put("organizationTypes", map.get("types"));
        }

        List<Map<String,Object>> organizationTypes = Collections.emptyList();
        Country country = countryGraphRepository.getCountryByOrganizationService(subServiceId);
        if(country != null){
            OrganizationTypeHierarchyQueryResult organizationTypeHierarchyQueryResult = organizationTypeGraphRepository.getOrganizationTypeHierarchy(country.getId(),organizationSubTypes);
            organizationTypes = organizationTypeHierarchyQueryResult.getOrganizationTypes();
        }
        response.put("organizationTypes",organizationTypes);
        return response;
    }
}
