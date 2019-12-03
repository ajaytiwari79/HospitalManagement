package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class StaffingLevelSetting {
    private int defaultDetailLevelMinutes=15;
    private Integer detailLevelMinutes;
    private Duration duration;
    private Map<BigInteger,Integer> activitiesRank;

    public StaffingLevelSetting(Integer detailLevelMinutes, Duration duration) {
        this.detailLevelMinutes = detailLevelMinutes;
        this.duration = duration;
    }


}
