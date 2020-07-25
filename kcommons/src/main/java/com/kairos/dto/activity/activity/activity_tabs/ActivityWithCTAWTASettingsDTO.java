package com.kairos.dto.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Created by prerna on 15/2/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ActivityWithCTAWTASettingsDTO {
    private BigInteger id;

    private String name;

    private String description;

    private ActivityCTAAndWTASettingsDTO activityCTAAndWTASettings;

    private BigInteger categoryId;

}
