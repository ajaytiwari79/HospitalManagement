package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.RiskSeverity;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiskBasicResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String riskRecommendation;
    private LocalDate dueDate;
    private boolean isReminderActive;
    private int daysToReminderBefore;
    private RiskSeverity riskLevel;


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getRiskRecommendation() { return riskRecommendation; }

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
