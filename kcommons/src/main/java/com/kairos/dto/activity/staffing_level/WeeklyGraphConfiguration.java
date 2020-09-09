package com.kairos.dto.activity.staffing_level;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyGraphConfiguration {
    private List<GraphIntervalConfiguration> overStaffingConfigurations;
    private List<GraphIntervalConfiguration> underStaffingConfigurations;
    private boolean unpublishedChanges;
    private boolean showStandBy;
    private boolean showOnCall;
    private int rightIntervalSetting;
    private int leftIntervalSetting;
    private int[] rightConfigurations;
    private int[] leftConfigurations;
}
