package com.kairos.controller.weather;

import com.kairos.service.weather.WeatherService;
import com.kairos.utils.response.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.ApiConstants.API_ORGANIZATION_UNIT_URL;


/**
 * Created By G.P.Ranjan on 8/4/20
 **/
@RestController
@RequestMapping(API_ORGANIZATION_UNIT_URL)
@Api(API_ORGANIZATION_UNIT_URL)
public class WeatherController {
    @Inject
    private WeatherService weatherService;

    @GetMapping("/today_weather")
    @ApiOperation("get weather info of unit")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getTodayWeatherInfo(@PathVariable Long unitId) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, weatherService.getTodayWeatherInfo(unitId));
    }

    @GetMapping("/weathers")
    @ApiOperation("get all weather info of unit in dates")
    // @PreAuthorize("@customPermissionEvaluator.isAuthorized()")
    public ResponseEntity<Map<String, Object>> getAllWeatherInfoBetweenDate(@PathVariable Long unitId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, weatherService.getAllWeatherInfoBetweenDate(unitId, startDate, isNull(endDate)?startDate:endDate));
    }

    @GetMapping(value = "/save_today_weather_info")
    @ApiOperation("save today weather info if not exist")
    public ResponseEntity<Map<String, Object>> saveTodayWeatherInfoOfAllUnit() {
        weatherService.saveTodayWeatherInfoOfAllUnit();
        return ResponseHandler.generateResponse(HttpStatus.OK, true, null);
    }
}
