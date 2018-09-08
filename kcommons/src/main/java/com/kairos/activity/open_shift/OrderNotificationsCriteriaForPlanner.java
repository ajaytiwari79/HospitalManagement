package com.kairos.activity.open_shift;

public class OrderNotificationsCriteriaForPlanner {

    private boolean confirmCandidateBeforePlanning;
    private boolean sendSMSOnNoCandidateBeforeDeadline;
    private boolean sendSmsOnCandidateFound;

    public boolean isSendSmsOnCandidateFound() {
        return sendSmsOnCandidateFound;
    }

    public void setSendSmsOnCandidateFound(boolean sendSmsOnCandidateFound) {
        this.sendSmsOnCandidateFound = sendSmsOnCandidateFound;
    }


    public boolean isConfirmCandidateBeforePlanning() {
        return confirmCandidateBeforePlanning;
    }

    public void setConfirmCandidateBeforePlanning(boolean confirmCandidateBeforePlanning) {
        this.confirmCandidateBeforePlanning = confirmCandidateBeforePlanning;
    }


    public boolean isSendSMSOnNoCandidateBeforeDeadline() {
        return sendSMSOnNoCandidateBeforeDeadline;
    }

    public void setSendSMSOnNoCandidateBeforeDeadline(boolean sendSMSOnNoCandidateBeforeDeadline) {
        this.sendSMSOnNoCandidateBeforeDeadline = sendSMSOnNoCandidateBeforeDeadline;
    }

}
