package com.kairos.dto.activity.staffing_level;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyGraphConfiguration {
    int percentageMoreThanTheMaxSL;
    int percentageFromMoreThanMaxSL;
    int percentageToMoreThanMaxSL;
    int percentageOrMoreThanMaxSL;
    int percentageOfMinSL;
    int percentageFromMinSL;
    int percentageToMinSL;
    int moreThanPercentageOfMinSL;
    private boolean unpublishedChanges;
    private boolean showStandBy;
    private boolean showOnCall;
}
