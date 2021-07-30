package com.kairos.dto.activity.unit_settings;

import com.kairos.enums.TimeBankLimitsType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnitGeneralSettingDTO {
    private BigInteger id;
    private Long unitId;
    private TimeBankLimitsType timeBankLimitsType;
}
