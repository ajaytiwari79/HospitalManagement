package com.kairos.dto.activity.cta_compensation_setting;

import com.kairos.enums.DurationType;
import com.kairos.enums.cta.CompensationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CTACompensationConfiguration {

    private int from;
    private int to;
    private DurationType intervalType;
    private CompensationType compensationType;
    private int value;
}
