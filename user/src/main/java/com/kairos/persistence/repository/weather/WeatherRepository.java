package com.kairos.persistence.repository.weather;

import com.kairos.persistence.model.weather.WeatherInfo;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Created By G.P.Ranjan on 8/4/20
 **/
@Repository
public interface WeatherRepository extends Neo4jBaseRepository<WeatherInfo, Long> {

    @Query("MATCH (weatherInfo:WeatherInfo) WHERE weatherInfo.unitId={0} AND weatherInfo.date={1} \n" +
            "RETURN weatherInfo")
    WeatherInfo findByUnitIdAndDate(Long unitId, LocalDate date);

    @Query("MATCH (weatherInfo:WeatherInfo) WHERE weatherInfo.unitId={0} AND weatherInfo.date>={1} AND weatherInfo.date<={2} \n" +
            "RETURN weatherInfo")
    List<WeatherInfo> findAllByUnitIdAndDates(Long unitId, LocalDate startDate, LocalDate endDate);

}
