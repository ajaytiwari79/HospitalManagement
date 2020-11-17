package com.kairos.dto.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by pawanmandhan on 23/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ActivityIndividualPointsSettingsDTO {

    private Long activityId;
    private String individualPointsCalculationMethod;
    private Double numberOfFixedPoints;
}
