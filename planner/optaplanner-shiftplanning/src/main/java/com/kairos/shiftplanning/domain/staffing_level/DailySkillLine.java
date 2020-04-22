package com.kairos.shiftplanning.domain.staffing_level;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DailySkillLine extends DailyLine {
    private List<SkillLineInterval> skillLineIntervals;

    public DailySkillLine(LocalDate date, List<SkillLineInterval> skillLineIntervals) {
        this.date=date;
        this.skillLineIntervals = skillLineIntervals;
    }
}
