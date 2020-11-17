package com.kairos.dto.activity.staffing_level;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyGraphConfiguration {

    private boolean showStaffingLevelMin;
    private boolean showPlanningPeriodSolvedInPercentage;
    private boolean showPlanningPeriodRemainingInPercentage;
    private boolean fullFilled;
    private boolean overStaffing;
    private boolean unpublishedChanges;
    private boolean showStandBy;
    private boolean showOnCall;
    private int rightIntervalSetting;
    private int leftIntervalSetting;
    private int startInterval;
    private int endInterval;
    private int[] rightConfigurations;
    private int[] leftConfigurations;

}
