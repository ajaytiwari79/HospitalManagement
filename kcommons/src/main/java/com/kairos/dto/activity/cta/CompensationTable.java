package com.kairos.dto.activity.cta;

import com.kairos.dto.user.country.agreement.cta.CompensationMeasurementType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Getter
@Setter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@NoArgsConstructor
public class CompensationTable {
    private int granularityLevel;
    private List<CompensationTableInterval> compensationTableInterval=new ArrayList<>();
    //use for protected days off calculation
    private CompensationMeasurementType unusedDaysOffType;
    private float unusedDaysOffvalue;

}
