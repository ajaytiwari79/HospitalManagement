package com.kairos.dto.activity.open_shift;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class DeadlineRule {
    LocalDate deadline;
    private Integer daysBeforeStart;
    private boolean expiresIfNoCandidateAfterDeadline;

}
