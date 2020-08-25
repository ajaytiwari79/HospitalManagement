package com.kairos.enums.cta;

import com.kairos.dto.activity.cta.FixedValue;
import com.kairos.dto.activity.cta_compensation_setting.CTACompensationConfiguration;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CalculateValueAgainst{
    private CalculateValueType calculateValue;
    private float scale;
    private FixedValue fixedValue;
    private List<CTACompensationConfiguration> ctaCompensationConfigurations;
    
}
