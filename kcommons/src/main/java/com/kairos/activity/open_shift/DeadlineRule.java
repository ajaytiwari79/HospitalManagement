package com.kairos.activity.open_shift;

import java.time.LocalDate;

public class DeadlineRule {
    LocalDate deadline;
    private Integer daysBeforeStart;
    private boolean expiresIfNoCandidateAfterDeadline;

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Integer getDaysBeforeStart() {
        return daysBeforeStart;
    }

    public void setDaysBeforeStart(Integer daysBeforeStart) {
        this.daysBeforeStart = daysBeforeStart;
    }

    public boolean isExpiresIfNoCandidateAfterDeadline() {
        return expiresIfNoCandidateAfterDeadline;
    }

    public void setExpiresIfNoCandidateAfterDeadline(boolean expiresIfNoCandidateAfterDeadline) {
        this.expiresIfNoCandidateAfterDeadline = expiresIfNoCandidateAfterDeadline;
    }

}
