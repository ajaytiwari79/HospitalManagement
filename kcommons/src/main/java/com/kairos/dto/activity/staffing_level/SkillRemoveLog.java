package com.kairos.dto.activity.staffing_level;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillRemoveLog {
    private Long skillId;
    private Date date;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return new EqualsBuilder()
                .append(skillId, ((SkillRemoveLog) o).skillId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillId);
    }
}
