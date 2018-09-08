package com.kairos.activity.staffing_level;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;

public class StaffingLevelTemplatePeriod {
    private LocalDate startDate;
    private LocalDate endDate;

    public StaffingLevelTemplatePeriod() {
        //default constructor
    }

    public StaffingLevelTemplatePeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("startDate", startDate)
                .append("endDate", endDate)
                .toString();
    }
}
