package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.BasicRiskDTO;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationLevelRiskDTO  extends BasicRiskDTO {

    @NotNull(message = "Mention Due date")
    private LocalDate dueDate;
    private boolean isReminderActive;
    private int daysToReminderBefore;

    public LocalDate getDueDate() { return dueDate; }

    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isReminderActive() { return isReminderActive; }

    public void setReminderActive(boolean reminderActive) { isReminderActive = reminderActive; }

    public int getDaysToReminderBefore() { return daysToReminderBefore; }

    public void setDaysToReminderBefore(int daysToReminderBefore) { this.daysToReminderBefore = daysToReminderBefore; }
}
