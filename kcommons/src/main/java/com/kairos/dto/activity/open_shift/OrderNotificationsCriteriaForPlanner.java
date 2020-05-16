package com.kairos.dto.activity.open_shift;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderNotificationsCriteriaForPlanner {

    private boolean confirmCandidateBeforePlanning;
    private boolean sendSMSOnNoCandidateBeforeDeadline;
    private boolean sendSmsOnCandidateFound;

}
