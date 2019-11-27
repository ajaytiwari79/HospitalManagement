package com.kairos.dto.user.skill;

import com.kairos.enums.SkillLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created By G.P.Ranjan on 4/11/19
 **/
@Getter
@Setter
public class SkillLevelDTO {
    private Long skillId;
    private SkillLevel skillLevel;
    private LocalDate startDate;
    private LocalDate endDate;
}
