package com.kairos.wrapper.phase;

import com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class PhaseSettingsActivityDTO {

    private String id;
    private BigInteger activityId;
    private BigInteger phaseId;
    private List<PhaseTemplateValue> phaseTemplateValues;


}
