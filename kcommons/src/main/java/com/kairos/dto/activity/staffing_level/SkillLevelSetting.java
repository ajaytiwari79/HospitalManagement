package com.kairos.dto.activity.staffing_level;

import com.kairos.enums.SkillLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created By G.P.Ranjan on 19/10/19
 **/
@Getter
@Setter
@NoArgsConstructor
public class SkillLevelSetting {
    private int noOfStaff;
    private SkillLevel skillLevel;
}
