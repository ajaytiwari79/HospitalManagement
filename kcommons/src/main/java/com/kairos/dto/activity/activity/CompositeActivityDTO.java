package com.kairos.dto.activity.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.activity.activity_tabs.ActivityGeneralSettingsDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by pavan on 8/2/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CompositeActivityDTO implements Serializable {
    private BigInteger id;
    // TODO CHECK HOW TO UPDATE ID DYNAMICALLY
    private BigInteger compositeId;
    private String name;
    private String description;
    private Long countryId;
    private BigInteger categoryId;
    private String categoryName;
    private Long unitId;
    private ActivityGeneralSettingsDTO activityGeneralSettings;
    private Long countryActivityId;
    private Boolean allowedBefore;
    private Boolean allowedAfter;
}
