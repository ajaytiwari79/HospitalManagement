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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);
    private static final String API_KEY = "b6907d289e10d714a6e88b30761fae22";
    private static final String WEATHER_API = "https://samples.openweathermap.org/data/2.5/weather";

    public WeatherInfoDTO getTodayWeatherInfo(Long unitId) {
        WeatherInfo weatherInfo = weatherRepository.getTodayWeatherInfoByUnitId(unitId);
        if(isNull(weatherInfo)){
            weatherInfo = getTodayWeatherInfoFromWeatherAPI(unitId);
            if(isNotNull(weatherInfo)){
                weatherRepository.save(weatherInfo);
            }else{
                exceptionService.actionNotPermittedException(ERROR_WEATHER_NOTFOUND);
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
            RestTemplate restTemplate = new RestTemplate();
            String uri = WEATHER_API + "?q=" + org.getContactAddress().getCity() + "&appid=" + API_KEY;
            String weatherInfoInJSON = restTemplate.getForObject(uri, String.class);
            Map responseMap = ObjectMapperUtils.jsonStringToObject(weatherInfoInJSON, Map.class);
            if(Integer.parseInt(responseMap.get("cod").toString()) == 200){
                weatherInfo = new WeatherInfo(unitId, getLocalDate(), weatherInfoInJSON);
            }
        }catch (Exception ex){
            LOGGER.info("Exception --------> {}",ex.getMessage());
        }
        return weatherInfo;
    }

    public List<WeatherInfoDTO> getAllWeatherInfoBetweenDate(Long unitId, LocalDate startDate, LocalDate endDate) {
        if(startDate.isAfter(getLocalDate()) || endDate.isAfter(getLocalDate()) || endDate.isBefore(startDate)){
            exceptionService.actionNotPermittedException(ERROR_RESOURCE_DATE_INCORRECT);
        }
        List<WeatherInfo> weatherInfos = weatherRepository.findAllByUnitIdAndDates(unitId, startDate.toString(), endDate.toString());
        List<WeatherInfoDTO> weatherInfoDTOS = new ArrayList<>();
        for (WeatherInfo weatherInfo : weatherInfos) {
            WeatherInfoDTO weatherInfoDTO = ObjectMapperUtils.copyPropertiesByMapper(weatherInfo, WeatherInfoDTO.class);
            weatherInfoDTO.setWeatherInfo(ObjectMapperUtils.jsonStringToObject(weatherInfo.getWeatherInfoInJson(), Map.class));
            weatherInfoDTOS.add(weatherInfoDTO);
        }
        return weatherInfoDTOS;
    }

    public void saveTodayWeatherInfoOfAllUnit(){
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
