package com.kairos.dto.activity.unit_settings.activity_configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ActivityConfigurationDTO {
    private BigInteger id;
    private Long unitId;
    private PresencePlannedTime presencePlannedTime;
    private AbsencePlannedTime absencePlannedTime;
    private NonWorkingPlannedTime nonWorkingPlannedTime;
    private Long countryId;
}

