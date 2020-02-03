package com.kairos.shiftplanning.domain.staffing_level;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyStaffingLine {
    private DailyActivityLine dailyActivityLine;
    private DailySkillLine dailySkillLine;


}