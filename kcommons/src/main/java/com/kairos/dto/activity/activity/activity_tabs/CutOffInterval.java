package com.kairos.dto.activity.activity.activity_tabs;

import java.time.LocalDate;

/**
 * @author pradeep
 * @date - 21/8/18
 */

public class CutOffInterval {

    private LocalDate startDate;
    private LocalDate endDate;

    public CutOffInterval() {
    }

    public CutOffInterval(LocalDate startDate, LocalDate endDate) {
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
}
