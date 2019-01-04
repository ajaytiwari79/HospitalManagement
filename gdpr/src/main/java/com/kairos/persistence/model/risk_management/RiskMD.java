package com.kairos.persistence.model.risk_management;

import com.kairos.enums.RiskSeverity;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivityMD;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetTypeMD;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Entity
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

    @ManyToOne
    @JoinColumn(name="assetTypeId")
    private AssetTypeMD assetType;

    @ManyToOne
    @JoinColumn(name="processingActivityId")
    private ProcessingActivityMD processingActivity;


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

    public AssetTypeMD getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetTypeMD assetType) {
        this.assetType = assetType;
    }

    public ProcessingActivityMD getProcessingActivity() {
        return processingActivity;
    }

    public void setProcessingActivity(ProcessingActivityMD processingActivity) {
        this.processingActivity = processingActivity;
    }
}
