package com.kairos.persistence.model.user.expertise.response;

import com.kairos.enums.SkillLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
@QueryResult
public class SkillLevelQueryResult {
    private LocalDate startDate;
    private LocalDate endDate;
    private SkillLevel skillLevel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillLevelQueryResult that = (SkillLevelQueryResult) o;
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
