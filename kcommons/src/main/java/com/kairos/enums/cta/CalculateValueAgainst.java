package com.kairos.enums.cta;

import com.kairos.dto.activity.cta.FixedValue;
import lombok.*;

import java.util.Objects;

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
    
}
