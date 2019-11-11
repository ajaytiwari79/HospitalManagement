package com.kairos.dto.user.country.skill;

import com.kairos.enums.SkillLevel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
public class SkillLevelDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private SkillLevel skillLevel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillLevelDTO that = (SkillLevelDTO) o;
        return skillLevel == that.skillLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillLevel);
    }

    @AssertTrue(message = "message.start_date.less_than.end_date")
    public boolean isValid() {
        if (Optional.ofNullable(this.startDate).isPresent() && (Optional.ofNullable(this.endDate).isPresent())) {
            return !startDate.isAfter(endDate);
        }
        return true;
    }


}
