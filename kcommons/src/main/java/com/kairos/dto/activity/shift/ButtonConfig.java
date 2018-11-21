package com.kairos.dto.activity.shift;

public class ButtonConfig {
    private boolean sendToPayrollEnabled = false;

    public boolean isSendToPayrollEnabled() {
        return sendToPayrollEnabled;
    }

    public void setSendToPayrollEnabled(boolean sendToPayrollEnabled) {
        this.sendToPayrollEnabled = sendToPayrollEnabled;
    }
}
