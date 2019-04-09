package com.kairos.shiftplanning.domain.staffing_level;

import org.joda.time.LocalDate;

import java.util.List;

public class DailySkillLine extends DailyLine {
    private List<SkillLineInterval> skillLineIntervals;

    public DailySkillLine() {
    }

    public DailySkillLine(LocalDate date, List<SkillLineInterval> skillLineIntervals) {
        this.date=date;
        this.skillLineIntervals = skillLineIntervals;
    }

    public List<SkillLineInterval> getSkillLineIntervals() {
        return skillLineIntervals;
    }

    public void setSkillLineIntervals(List<SkillLineInterval> skillLineIntervals) {
        this.skillLineIntervals = skillLineIntervals;
    }
}
