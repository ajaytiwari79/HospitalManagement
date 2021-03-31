package com.kairos.persistence.model.weather;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

import java.time.LocalDate;

/**
 * Created By G.P.Ranjan on 8/4/20
 **/
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherInfo extends UserBaseEntity {
    private static final long serialVersionUID = -1131929101474906935L;
    private Long unitId;
    private LocalDate date;
    private String weatherInfoInJson;
}
