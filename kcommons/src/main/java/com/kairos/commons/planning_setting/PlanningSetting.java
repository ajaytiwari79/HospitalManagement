package com.kairos.commons.planning_setting;

import com.kairos.enums.constraint.ConstraintLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mockito.internal.exceptions.stacktrace.ConditionalStackTraceFilter;

@Getter
@Setter
@NoArgsConstructor
public class PlanningSetting {
    private ConstraintLevel constraintLevel;
    private int constraintWeight;


}
