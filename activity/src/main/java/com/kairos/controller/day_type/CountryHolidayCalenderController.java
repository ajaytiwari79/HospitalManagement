package com.kairos.controller.day_type;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.service.day_type.CountryHolidayCalenderService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigInteger;
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
   private CountryHolidayCalenderService countryHolidayCalenderService;



    // CountryHolidayCalender based on countryId
    @RequestMapping(value = COUNTRY_URL+"/holiday/year/{year}",method = RequestMethod.GET)
    @ApiOperation("Get all CountryHolidayCalenderController holiday by id & Year")
    ResponseEntity<Map<String, Object>> getCountryHolidaysByIdAndYear(@PathVariable int year, @PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,countryHolidayCalenderService.getAllCountryHolidaysByCountryIdAndYear(year,countryId));
    }



    // CountryHolidayCalender based on countryId
    @RequestMapping(value =COUNTRY_URL+"/holiday/all",method = RequestMethod.GET)
    @ApiOperation("Get all CountryHolidayCalenderController holiday All")
    ResponseEntity<Map<String, Object>> getCountryAllHolidays(@PathVariable Long countryId){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,countryHolidayCalenderService.getAllCountryAllHolidaysByCountryId(countryId));
    }

    // CountryHolidayCalender based on countryId
    @RequestMapping(value =UNIT_URL+"/holiday/all",method = RequestMethod.GET)
    @ApiOperation("Get all CountryHolidayCalenderController holiday All")
    ResponseEntity<Map<String, Object>> getUnitAllHolidays(){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,countryHolidayCalenderService.getAllCountryAllHolidaysByCountryId(null));
    }

      // Trigger Google Calender Service
//    @RequestMapping(value = COUNTRY_URL+"/holiday/fetch",method = RequestMethod.GET)
//    @ApiOperation("Get all CountryHolidayCalenderController holiday All")
//    ResponseEntity<Map<String, Object>> triggerGoogleCalenderService(@PathVariable Long countryId){
//        return ResponseHandler.generateResponse(HttpStatus.OK,true,countryService.triggerGoogleCalenderService(countryId));
//    }







    @RequestMapping(value = COUNTRY_URL+"/holiday",method = RequestMethod.POST)
    @ApiOperation("save a new CountryHolidayCalenderController holiday by id")
    ResponseEntity<Map<String, Object>> addCountryCalenderToCountry(@PathVariable Long countryId ,@RequestBody CountryHolidayCalenderDTO countryHolidayCalender){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,countryHolidayCalenderService.createHolidayCalenderByCountryId(countryId,countryHolidayCalender));
    }
    @RequestMapping(value = COUNTRY_URL+"/holiday",method = RequestMethod.PUT)
    @ApiOperation("Update a new CountryHolidayCalenderController holiday by id")
    ResponseEntity<Map<String, Object>> updateCountryCalender(@PathVariable Long countryId ,@RequestBody CountryHolidayCalenderDTO countryHolidayCalender){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,countryHolidayCalenderService.updateCountryCalender(countryId, countryHolidayCalender));
    }
    @RequestMapping(value = COUNTRY_URL+"/holiday/{holidayId}",method = RequestMethod.DELETE)
    @ApiOperation("Delete a  CountryHolidayCalenderController holiday by id")
    ResponseEntity<Map<String, Object>> deleteCountryCalender(@PathVariable BigInteger holidayId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, countryHolidayCalenderService.safeDeleteCountryCalender(holidayId));
    }
}
