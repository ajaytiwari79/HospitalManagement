package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.BasicRiskDTO;
import com.kairos.dto.gdpr.Staff;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationLevelRiskDTO  extends BasicRiskDTO {

    @NotNull(message = "error.message.risk.due.date")
    private LocalDate dueDate;
    private Staff riskOwner;
    private boolean reminderActive;
    private int daysToReminderBefore;

    public Staff getRiskOwner() { return riskOwner; }

    public void setRiskOwner(Staff riskOwner) { this.riskOwner = riskOwner; }

    public LocalDate getDueDate() { return dueDate; }

    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isReminderActive() { return reminderActive; }

    public void setReminderActive(boolean reminderActive) { this.reminderActive = reminderActive; }

    public int getDaysToReminderBefore() { return daysToReminderBefore; }

    public void setDaysToReminderBefore(int daysToReminderBefore) { this.daysToReminderBefore = daysToReminderBefore; }
}
