package com.kairos.dto.user.skill;

import com.kairos.enums.SkillLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

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

    public boolean isValidSkillsByLocalDate(LocalDate localDate){
        return (isNull(this.getEndDate()) && !this.getStartDate().isAfter(localDate)) || (isNotNull(this.getEndDate()) && !this.getStartDate().isAfter(localDate) && !this.getEndDate().isBefore(localDate));
    }
}
