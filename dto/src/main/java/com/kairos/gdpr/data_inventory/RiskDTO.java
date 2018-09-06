package com.kairos.gdpr.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.RiskSeverity;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RiskDTO {

    private BigInteger id;

    @NotBlank(message = "Name can't be Empty")
    private String name;

    @NotBlank(message = "Description can't be Empty")
    private String description;

    @NotBlank(message = "Mention Risk Recommendation")
    private String riskRecommendation;

    private LocalDate dueDate;

    private boolean isReminderActive;

    private int daysToReminderBefore;

    private RiskSeverity riskLevel;

    public String getName() { return name.trim(); }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description.trim(); }

    public void setDescription(String description) { this.description = description; }

    public String getRiskRecommendation() { return riskRecommendation.trim(); }

    public void setRiskRecommendation(String riskRecommendation) { this.riskRecommendation = riskRecommendation; }

    public LocalDate getDueDate() { return dueDate; }

    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isReminderActive() { return isReminderActive; }

    public void setReminderActive(boolean reminderActive) { isReminderActive = reminderActive; }

    public int getDaysToReminderBefore() { return daysToReminderBefore; }

    public void setDaysToReminderBefore(int daysToReminderBefore) { this.daysToReminderBefore = daysToReminderBefore; }

    public RiskSeverity getRiskLevel() { return riskLevel; }

    public void setRiskLevel(RiskSeverity riskLevel) { this.riskLevel = riskLevel; }
}
