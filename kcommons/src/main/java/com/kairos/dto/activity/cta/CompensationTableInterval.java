package com.kairos.dto.activity.cta;

import com.kairos.dto.user.country.agreement.cta.CompensationMeasurementType;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CompensationTableInterval {
    private LocalTime from;
    private LocalTime to;
    private float value;
    private CompensationMeasurementType compensationMeasurementType;


}
