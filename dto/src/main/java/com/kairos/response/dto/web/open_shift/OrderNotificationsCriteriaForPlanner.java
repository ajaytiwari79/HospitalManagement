package com.kairos.response.dto.web.open_shift;

public class OrderNotificationsCriteriaForPlanner {

    private boolean confirmCandidateBeforePlanning;
    private boolean sendSMSOnNoCandidateBeforeDeadline;

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
