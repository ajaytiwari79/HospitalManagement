package com.kairos.persistence.model.risk_management;

import com.kairos.dto.gdpr.Staff;
import com.kairos.enums.RiskSeverity;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Document
public class Risk extends MongoBaseEntity {

    @NotBlank(message = "Name can't be Empty")
    private String name;
    @NotBlank(message = "Description can't be Empty")
    private String description;
    private Long countryId;
    @NotBlank(message = "Mention Recommendation")
    private String riskRecommendation;
    private LocalDate dueDate;
    private boolean isReminderActive;
    private int daysToReminderBefore;
    private Staff riskOwner;
    @NotNull(message = "Risk Level can't be null")
    private RiskSeverity riskLevel;

    public Staff getRiskOwner() { return riskOwner; }

    public void setRiskOwner(Staff riskOwner) { this.riskOwner = riskOwner; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRiskRecommendation() {
        return riskRecommendation;
    }

    public void setRiskRecommendation(String riskRecommendation) {
        this.riskRecommendation = riskRecommendation;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isReminderActive() {
        return isReminderActive;
    }

    public void setReminderActive(boolean reminderActive) {
        isReminderActive = reminderActive;
    }

    public int getDaysToReminderBefore() {
        return daysToReminderBefore;
    }

    public void setDaysToReminderBefore(int daysToReminderBefore) {
        this.daysToReminderBefore = daysToReminderBefore;
    }

    public RiskSeverity getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskSeverity riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Risk(Long countryId, @NotBlank(message = "Name can't be Empty") String name, @NotBlank(message = "Description can't be Empty") String description,
                @NotBlank(message = "Mention Recommendation") String riskRecommendation, @NotNull(message = "Risk Level can't be null") RiskSeverity riskLevel) {
        this.name = name;
        this.description = description;
        this.riskRecommendation = riskRecommendation;
        this.riskLevel = riskLevel;
        this.countryId = countryId;
    }

    public Risk(@NotBlank(message = "Name can't be Empty") String name, @NotBlank(message = "Description can't be Empty") String description,
                @NotBlank(message = "Mention Recommendation") String riskRecommendation, @NotNull(message = "Risk Level can't be null") RiskSeverity riskLevel,LocalDate dueDates) {
        this.name = name;
        this.description = description;
        this.riskRecommendation = riskRecommendation;
        this.riskLevel = riskLevel;
        this.dueDate=dueDates;
    }

    public Risk() {
    }
}
