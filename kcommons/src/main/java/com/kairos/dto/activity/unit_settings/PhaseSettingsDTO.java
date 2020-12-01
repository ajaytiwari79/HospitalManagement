package com.kairos.dto.activity.unit_settings;

import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PhaseSettingsDTO {
    private BigInteger id;
    private BigInteger phaseId;
    private String name;
    private String description;
    private boolean staffEligibleForUnderStaffing;
    private boolean staffEligibleForOverStaffing;
    private boolean managementEligibleForUnderStaffing;
    private boolean managementEligibleForOverStaffing;
    private Long unitId;
    private int sequence;
    private Map<String, TranslationInfo> translations;
}
