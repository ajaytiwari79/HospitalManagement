package com.kairos.persistence.model.risk_management;

import com.kairos.enums.RiskSeverity;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.response.dto.common.RiskResponseDTO;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@NamedNativeQuery(name = "getAllRiskData", resultClass = RiskResponseDTO.class, query = "select risk.id as id ,risk.name as name, risk.description as description, risk.risk_recommendation as riskRecommendation,  risk.is_reminder_active as isReminderActive,risk.days_to_reminder_before as daysToReminderBefore,/* risk.risk_level as riskLevel,*/PA.name as processingActivityName, PA.id as processingActivityId, PA.sub_processing_activity as isSubProcessing from riskmd risk inner join processing_activitymd_risks PAR ON PAR.risks_id = risk.id left join processing_activitymd PA ON PAR.processing_activitymd_id = PA.id where risk.organization_id = ?1 and risk.deleted = false", resultSetMapping = "getAllRiskData")
@SqlResultSetMapping(
        name = "getAllRiskData",
        classes = @ConstructorResult(
                targetClass = RiskResponseDTO.class,
                columns = {
                        @ColumnResult(name = "id"),  @ColumnResult(name = "name"), @ColumnResult(name = "description"), @ColumnResult(name = "riskRecommendation"),  @ColumnResult(name = "isReminderActive"),  @ColumnResult(name = "daysToReminderBefore"),
                        /*@ColumnResult(name = "riskLevel"),*/  @ColumnResult(name = "processingActivityName"),
                        @ColumnResult(name = "processingActivityId"), @ColumnResult(name = "isSubProcessing")

                }
        )
)
public class RiskMD extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    private Long countryId;
    @NotBlank(message = "error.message.risk.recommendation")
    private String riskRecommendation;
    private boolean isReminderActive;
    private int daysToReminderBefore;
   // private LocalDate dueDate;
    // private Staff riskOwner;
    @NotNull(message = "error.message.risk.level")
    private RiskSeverity riskLevel;

    public RiskMD() {
    }

    public RiskMD(Long countryId, @NotBlank(message = "Name can't be Empty") String name, @NotBlank(message = "Description can't be Empty") String description,
                  @NotBlank(message = "Mention Recommendation") String riskRecommendation, @NotNull(message = "RISK Level can't be null") RiskSeverity riskLevel) {
        this.name = name;
        this.description = description;
        this.riskRecommendation = riskRecommendation;
        this.riskLevel = riskLevel;
        this.countryId = countryId;
    }


    public RiskMD(@NotBlank(message = "Name can't be Empty") String name, @NotBlank(message = "Description can't be Empty") String description,
                  @NotBlank(message = "Mention Recommendation") String riskRecommendation, @NotNull(message = "RISK Level can't be null") RiskSeverity riskLevel) {
        this.name = name;
        this.description = description;
        this.riskRecommendation = riskRecommendation;
        this.riskLevel = riskLevel;
    }

    public RiskMD(Long id){
        this.id= id;
    }

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

}
