package com.kairos.dto.activity.granularity_setting;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GranularitySettingDTO {
    private BigInteger id;
    private int granularityInMinute;
    private Long countryId;
    private Long organisationTypeId;
    private Long unitId;
    private LocalDate startDate;
    private LocalDate endDate;
}
