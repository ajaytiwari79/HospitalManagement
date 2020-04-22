package com.kairos.shiftplanning.domain.staffing_level;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DailyStaffingLine {
    private DailyActivityLine dailyActivityLine;
    private DailySkillLine dailySkillLine;


}