package com.kairos.service.weather;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.weather.WeatherInfoDTO;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.weather.WeatherInfo;
import com.kairos.persistence.repository.weather.WeatherRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getLocalDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.UserMessagesConstants.ERROR_RESOURCE_DATE_INCORRECT;
import static com.kairos.constants.UserMessagesConstants.ERROR_WEATHER_NOTFOUND;


/**
 * Created By G.P.Ranjan on 8/4/20
 **/
@Service
public class WeatherService {
    @Inject
    private WeatherRepository weatherRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private RestTemplate restTemplate;
    @Value("${weather.api.key}")
    private String weatherApiKey;
    @Value("${weather.api}")
    private String weatherApi ;

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);

    private Map<String,String> mapOfCityAndWeatherInfo = new HashMap<>();

    public WeatherInfoDTO getTodayWeatherInfo(Long unitId) {
        WeatherInfo weatherInfo = weatherRepository.getTodayWeatherInfoByUnitId(unitId);
        if(isNull(weatherInfo)){
            weatherInfo = getTodayWeatherInfoFromWeatherAPI(unitId);
            if(isNotNull(weatherInfo)){
                weatherRepository.save(weatherInfo);
            }else{
                exceptionService.dataNotFoundByIdException(ERROR_WEATHER_NOTFOUND);
            }
        }
        WeatherInfoDTO weatherInfoDTO = ObjectMapperUtils.copyPropertiesByMapper(weatherInfo, WeatherInfoDTO.class);
        weatherInfoDTO.setWeatherInfo(ObjectMapperUtils.jsonStringToObject(weatherInfo.getWeatherInfoInJson(), Map.class));
        return weatherInfoDTO;
    }

    private WeatherInfo getTodayWeatherInfoFromWeatherAPI(Long unitId){
        OrganizationBaseEntity  org = organizationService.getOrganizationById(unitId);
        WeatherInfo weatherInfo = null;
        try{
            String city = org.getContactAddress().getCity();
            if(city.indexOf(' ') != -1) {
                city = city.substring(0, city.lastIndexOf(' '));
            }
            if(!mapOfCityAndWeatherInfo.containsKey(city)) {
                String weatherApiUrl = weatherApi + "?q=" + city + "&appid=" + weatherApiKey;
                LOGGER.info("Weather URL is ======> {}",weatherApiUrl);
                Map responseData=restTemplate.getForObject(weatherApiUrl, Map.class);
                //remove this call if api is call for 16 day
                removeDuplicateDataFromResponse(responseData);
                mapOfCityAndWeatherInfo.put(city, ObjectMapperUtils.objectToJsonString(responseData));
            }
            weatherInfo = new WeatherInfo(unitId, getLocalDate(), mapOfCityAndWeatherInfo.get(city));
        }catch (Exception ex){
            LOGGER.info("Exception --------> {}",ex.getMessage());
        }
        return weatherInfo;
    }
    //this method removed if response have no repeat data for one day.
    private void removeDuplicateDataFromResponse(Map responseData) {
        List<Map> list = (List<Map>) responseData.get("list");
        Map<String,Map> newList = new HashMap<>();
        for (Map map : list) {
            String date = map.get("dt_txt").toString();
            date = date.substring(0,10);
            newList.put(date,map);
        }
        responseData.put("list",newList.values());
    }

    public WeatherInfoDTO getWeatherInfoAtDate(Long unitId, LocalDate date) {
        if(date.isAfter(getLocalDate())){
            exceptionService.actionNotPermittedException(ERROR_RESOURCE_DATE_INCORRECT);
        }
        WeatherInfo weatherInfo = weatherRepository.findWeatherInfoByUnitIdAndDate(unitId, date.toString());
        if(isNull(weatherInfo)){
            exceptionService.dataNotFoundByIdException(ERROR_WEATHER_NOTFOUND);
        }
        WeatherInfoDTO weatherInfoDTO = ObjectMapperUtils.copyPropertiesByMapper(weatherInfo, WeatherInfoDTO.class);
        weatherInfoDTO.setWeatherInfo(ObjectMapperUtils.jsonStringToObject(weatherInfo.getWeatherInfoInJson(), Map.class));
        return weatherInfoDTO;
    }

    public void saveWeatherInfoOfAllUnit(){
        List<WeatherInfo> todayAllWeatherInfo = weatherRepository.getAllTodayWeatherInfo();
        Map<Long,WeatherInfo> mapOfUnitIdAndWeatherInfo = todayAllWeatherInfo.stream().collect(Collectors.toMap(WeatherInfo::getUnitId,v->v));
        List<Long> orgs = organizationService.getAllOrganizationIds();
        List<WeatherInfo> weatherInfos = new ArrayList<>();
        for (Long org : orgs) {
            if(!mapOfUnitIdAndWeatherInfo.containsKey(org)){
                WeatherInfo weatherInfo = getTodayWeatherInfoFromWeatherAPI(org);
                if(isNotNull(weatherInfo)){
                    weatherInfos.add(weatherInfo);
                }
            }
        }
        if(isCollectionNotEmpty(weatherInfos)){
            weatherRepository.saveAll(weatherInfos);
        }
    }
}
