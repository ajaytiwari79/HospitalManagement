package com.kairos.persistence.model.risk_management;

import com.kairos.enums.RiskSeverity;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.response.dto.common.RiskResponseDTO;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "getAllAssetTypeRisks",
                classes = @ConstructorResult(
                        targetClass = RiskResponseDTO.class,
                        columns = {
                                @ColumnResult(name = "id"),@ColumnResult(name = "name"), @ColumnResult(name = "description"), @ColumnResult(name = "riskRecommendation"),  @ColumnResult(name = "isReminderActive"),  @ColumnResult(name = "daysToReminderBefore"),@ColumnResult(name = "riskLevel"),
                                @ColumnResult(name = "assetTypeId", type= BigInteger.class),@ColumnResult(name = "assetTypeName", type=String.class), @ColumnResult(name = "isSubAssetType", type = boolean.class)
                        }
                )
        ),
        @SqlResultSetMapping(
                name = "getAllProcessingActivityRisks",
                classes = @ConstructorResult(
                        targetClass = RiskResponseDTO.class,
                        columns = {
                                @ColumnResult(name = "id"),@ColumnResult(name = "name"), @ColumnResult(name = "description"), @ColumnResult(name = "riskRecommendation"),  @ColumnResult(name = "isReminderActive"),  @ColumnResult(name = "daysToReminderBefore"),@ColumnResult(name = "riskLevel"),
                                @ColumnResult(name = "processingActivityName", type=String.class),@ColumnResult(name = "processingActivityId", type= BigInteger.class), @ColumnResult(name = "isSubProcessingActivity", type = boolean.class)
                        }
                )
        )
})

@NamedNativeQueries({
        @NamedNativeQuery(name = "getAllProcessingActivityRiskData",resultSetMapping = "getAllProcessingActivityRisks",resultClass = RiskResponseDTO.class, query = "Select RISK.id as id, RISK.name as name, RISK.description as description, RISK.risk_recommendation as riskRecommendation,RISK.is_reminder_active as isReminderActive,RISK.days_to_reminder_before as daysToReminderBefore,RISK.risk_level as riskLevel, PA.name as processingActivityName, PA.id as processingActivityId, PA.sub_process_activity as isSubProcessingActivity from risk RISK inner join master_processing_activity PA ON RISK.processing_activity_id = PA.id where RISK.organization_id = ?1 and RISK.deleted =false"),
        @NamedNativeQuery(name = "getAllAssetTypeRiskData",resultSetMapping = "getAllAssetTypeRisks",  resultClass = RiskResponseDTO.class, query = "Select RISK.id as id, RISK.name as name, RISK.description as description, RISK.risk_recommendation as riskRecommendation,RISK.is_reminder_active as isReminderActive,RISK.days_to_reminder_before as daysToReminderBefore,RISK.risk_level as riskLevel, AT.name as assetTypeName, AT.id as assetTypeId, AT.is_sub_asset_type as isSubAssetType from risk RISK inner join asset_type AT ON RISK.asset_type_id = AT.id where RISK.organization_id = ?1 and RISK.deleted =false")
})


public class Risk extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    private Long countryId;
    @NotBlank(message = "error.message.risk.recommendation")
    private String riskRecommendation;
    private boolean isReminderActive;
    private int daysToReminderBefore;
    @NotNull(message = "error.message.risk.level")
    private RiskSeverity riskLevel;
    private Long organizationId;


    public Risk(Long countryId, @NotBlank(message = "Name can't be Empty") String name, @NotBlank(message = "Description can't be Empty") String description,
                                             @NotBlank(message = "Mention Recommendation") String riskRecommendation, @NotNull(message = "RISK Level can't be null") RiskSeverity riskLevel) {
        this.name = name;
        this.description = description;
        this.riskRecommendation = riskRecommendation;
        this.riskLevel = riskLevel;
        this.countryId = countryId;
    }


    public Risk(@NotBlank(message = "Name can't be Empty") String name, @NotBlank(message = "Description can't be Empty") String description,
                @NotBlank(message = "Mention Recommendation") String riskRecommendation, @NotNull(message = "RISK Level can't be null") RiskSeverity riskLevel) {
        this.name = name;
        this.description = description;
        this.riskRecommendation = riskRecommendation;
        this.riskLevel = riskLevel;
    }

    public Risk() {
    }

    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

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
