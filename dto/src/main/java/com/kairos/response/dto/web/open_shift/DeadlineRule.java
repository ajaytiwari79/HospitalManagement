package com.kairos.response.dto.web.open_shift;

import java.time.LocalDate;

public class DeadlineRule {
    LocalDate Deadline;
    private Integer daysBeforeStart;
    private boolean expiresIfNoCandidateAfterDeadline;

    public LocalDate getDeadline() {
        return Deadline;
    }

    public void setDeadline(LocalDate deadline) {
        Deadline = deadline;
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
