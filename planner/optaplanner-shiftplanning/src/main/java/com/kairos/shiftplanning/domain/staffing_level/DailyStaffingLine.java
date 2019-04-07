package com.kairos.shiftplanning.domain.staffing_level;

public class DailyStaffingLine {
    private DailyActivityLine dailyActivityLine;
    private DailySkillLine dailySkillLine;

    public DailyStaffingLine(DailyActivityLine dailyActivityLine, DailySkillLine dailySkillLine) {
        this.dailyActivityLine = dailyActivityLine;
        this.dailySkillLine = dailySkillLine;
    }

    public DailyStaffingLine() {
    }

    public DailyActivityLine getDailyActivityLine() {
        return dailyActivityLine;
    }

    public void setDailyActivityLine(DailyActivityLine dailyActivityLine) {
        this.dailyActivityLine = dailyActivityLine;
    }

    public DailySkillLine getDailySkillLine() {
        return dailySkillLine;
    }

    public void setDailySkillLine(DailySkillLine dailySkillLine) {
        this.dailySkillLine = dailySkillLine;
    }
}