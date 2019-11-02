package com.kairos.dto.activity.staffing_level;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;

@Getter
@Setter
public class StaffingLevelTemplatePeriod {
    private LocalDate startDate;
    private LocalDate endDate;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("startDate", startDate)
                .append("endDate", endDate)
                .toString();
    }
}
