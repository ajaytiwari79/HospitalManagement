package com.kairos.dto.activity.unit_settings;

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PhaseSettingsDTO implements Serializable {
    private static final long serialVersionUID = 129261157436625112L;
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
    private short maxProblemAllowed; // storing it in minutes

    public String getName(){
        return TranslationUtil.getName(translations,name);
    }
    public String getDescription(){
        return TranslationUtil.getName(translations,description);
    }
}
