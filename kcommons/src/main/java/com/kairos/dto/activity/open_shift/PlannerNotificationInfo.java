package com.kairos.dto.activity.open_shift;

import com.kairos.enums.InformationReciever;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlannerNotificationInfo {
        private List<InformationReciever> informationReceivers;
        private DurationField sendNotificationOnCandidateMissingBeforeTime; //in Hours
        private DurationField notifyForUnassignedShiftBeforeTime; // in Hours
}
