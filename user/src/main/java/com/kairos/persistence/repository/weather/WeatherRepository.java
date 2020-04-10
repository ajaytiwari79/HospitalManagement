package com.kairos.persistence.repository.weather;

import com.kairos.persistence.model.weather.WeatherInfo;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Created By G.P.Ranjan on 8/4/20
 **/
@Repository
public interface WeatherRepository extends Neo4jBaseRepository<WeatherInfo, Long> {

    @Query("MATCH (weatherInfo:WeatherInfo) WHERE weatherInfo.unitId={0} AND date(weatherInfo.date)=date() \n" +
            "RETURN weatherInfo")
    WeatherInfo getTodayWeatherInfoByUnitId(Long unitId);

    @Query("MATCH (weatherInfo:WeatherInfo) WHERE weatherInfo.unitId={0} AND date(weatherInfo.date) >= date({1}) AND date(weatherInfo.date) <= date({2}) \n" +
            "RETURN weatherInfo")
    List<WeatherInfo> findAllByUnitIdAndDates(Long unitId, String startDate, String endDate);

    @Query("MATCH (weatherInfo:WeatherInfo) WHERE date(weatherInfo.date)=date() \n" +
            "RETURN weatherInfo")
    List<WeatherInfo> getAllTodayWeatherInfo();
}
