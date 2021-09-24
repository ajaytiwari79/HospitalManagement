package com.kairos.dto.activity.unit_settings;

import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Map;

@Getter
@Setter
public class UnitSettingDTO {
    private BigInteger id;
    private Long unitId;
    private OpenShiftPhaseSetting openShiftPhaseSetting;
    private FlexibleTimeSettingDTO flexibleTimeSettings;
}
