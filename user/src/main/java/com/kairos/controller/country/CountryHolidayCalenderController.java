package com.kairos.controller.country;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.service.country.CountryHolidayCalenderService;
import com.kairos.service.country.CountryService;
import com.kairos.service.google_calender.CountryCalenderService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

import static com.kairos.constants.ApiConstants.*;

/**
 *  CountryHolidayCalenderController
 *  1.Calls CountryHolidayCalenderService
 *  2. Call for CRUD operation on CountryHolidayCalender
 */
@RestController
@RequestMapping(API_V1 )
@Api(API_V1 )
public class CountryHolidayCalenderController {

    @Inject
    CountryHolidayCalenderService countryHolidayCalenderService;

    @Inject
    CountryService countryService;

    @Inject
    private CountryCalenderService countryCalenderService;



    // CountryHolidayCalender based on countryId
    @RequestMapping(value = COUNTRY_URL+"/holiday/year/{year}",method = RequestMethod.GET)
    @ApiOperation("Get all CountryHolidayCalenderController holiday by id & Year")
    ResponseEntity<Map<String, Object>> getCountryHolidaysByIdAndYear(@PathVariable int year, @PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,countryService.getAllCountryHolidaysByCountryIdAndYear(year,countryId));
    }

    // CountryHolidayCalender based on countryId
    @RequestMapping(value =COUNTRY_URL+"/holiday/all",method = RequestMethod.GET)
    @ApiOperation("Get all CountryHolidayCalenderController holiday All")
    ResponseEntity<Map<String, Object>> getCountryAllHolidays(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,countryService.getAllCountryAllHolidaysByCountryId(countryId));
    }

    // CountryHolidayCalender based on countryId
    @RequestMapping(value =UNIT_URL+"/holiday/all",method = RequestMethod.GET)
    @ApiOperation("Get all CountryHolidayCalenderController holiday All")
    ResponseEntity<Map<String, Object>> getUnitAllHolidays(@PathVariable Long unitId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,countryService.getAllUnitAllHolidaysByUnitId(unitId));
    }

      // Trigger Google Calender Service
    @RequestMapping(value = COUNTRY_URL+"/holiday/fetch",method = RequestMethod.GET)
    @ApiOperation("Get all CountryHolidayCalenderController holiday All")
    ResponseEntity<Map<String, Object>> triggerGoogleCalenderService(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,countryService.triggerGoogleCalenderService(countryId));
    }



    // Trigger Google Calender Service
//    @RequestMapping(value = "/holiday/fetch",method = RequestMethod.GET)
//    @ApiOperation("Get all CountryHolidayCalenderController holiday All")
//    ResponseEntity<Map<String, Object>> triggerGoogleCalenderService(@PathVariable Long countryId) throws IOException, ClassNotFoundException {
//        return ResponseHandler.generateResponse(HttpStatus.OK,true,countryCalenderService.authorize());
//    }




    @RequestMapping(value = COUNTRY_URL+"/holiday",method = RequestMethod.POST)
    @ApiOperation("save a new CountryHolidayCalenderController holiday by id")
    ResponseEntity<Map<String, Object>> addCountryCalenderToCountry(@PathVariable Long countryId ,@RequestBody CountryHolidayCalenderDTO countryHolidayCalender) throws Exception{
        return ResponseHandler.generateResponse(HttpStatus.OK,true,countryHolidayCalenderService.createHolidayCalenderByCountryId(countryId,countryHolidayCalender));
    }
    @RequestMapping(value = COUNTRY_URL+"/holiday",method = RequestMethod.PUT)
    @ApiOperation("Update a new CountryHolidayCalenderController holiday by id")
    ResponseEntity<Map<String, Object>> updateCountryCalender(@RequestBody CountryHolidayCalenderDTO countryHolidayCalender) throws Exception{
        return ResponseHandler.generateResponse(HttpStatus.OK,true,countryHolidayCalenderService.updateCountryCalender(countryHolidayCalender));
    }
    @RequestMapping(value = COUNTRY_URL+"holiday/{holidayId}",method = RequestMethod.DELETE)
    @ApiOperation("Delete a  CountryHolidayCalenderController holiday by id")
    ResponseEntity<Map<String, Object>> deleteCountryCalender(@PathVariable Long holidayId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryHolidayCalenderService.safeDeleteCountryCalender(holidayId));
    }
}
