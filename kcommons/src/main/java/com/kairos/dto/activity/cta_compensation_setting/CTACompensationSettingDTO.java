package com.kairos.dto.activity.cta_compensation_setting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CTACompensationSettingDTO {
    private BigInteger id;
    private List<CTACompensationConfiguration> configurations;
    private Long countryId;
    private Long expertiseId;
    private Long unitId;
}
