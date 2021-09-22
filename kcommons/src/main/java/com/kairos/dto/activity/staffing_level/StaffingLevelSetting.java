package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelSetting {
    private int defaultDetailLevelMinutes=15;
    private Integer detailLevelMinutes;
    private Duration duration;

    public StaffingLevelSetting(Integer detailLevelMinutes, Duration duration) {
        this.detailLevelMinutes = detailLevelMinutes;
        this.duration = duration;
    }


}
