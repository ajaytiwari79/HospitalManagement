package com.kairos.persistence.model;

import com.kairos.enums.phase.PhaseDefaultName;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ShiftDataHelper {
    private Map<LocalDate, Boolean> dateAndPublishPlanningPeriod;
    private Map<LocalDate, PhaseDefaultName> dateAndPhaseDefaultName;

}

