package com.kairos.dto.user.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

/**
 * Created By G.P.Ranjan on 8/4/20
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherInfoDTO {
    private Long id;
    private Long unitId;
    private LocalDate date;
    private Map weatherInfo;
}
