package com.kairos.response.dto.common;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.Staff;
import com.kairos.dto.gdpr.metadata.AssetTypeBasicDTO;
import com.kairos.enums.RiskSeverity;
import com.kairos.persistence.model.risk_management.RiskMD;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicDTO;
import com.kairos.response.dto.master_data.AssetTypeDTO;

import javax.persistence.ConstructorResult;
import java.math.BigInteger;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiskResponseDTO {

    private BigInteger id;
    private String name;
    private String description;
    private String riskRecommendation;
    private LocalDate dueDate;
    private boolean isReminderActive;
    private int daysToReminderBefore;
    private RiskSeverity riskLevel;
    private Staff riskOwner;
    private AssetTypeDTO assetType;
    private ProcessingActivityBasicDTO processingActivity;


    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id;}

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

    public Staff getRiskOwner() { return riskOwner; }

    public void setRiskOwner(Staff riskOwner) { this.riskOwner = riskOwner; }

    public AssetTypeDTO getAssetType() { return assetType; }

    public void setAssetType(AssetTypeDTO assetType) { this.assetType = assetType;}


    public ProcessingActivityBasicDTO getProcessingActivity() { return processingActivity; }

    public void setProcessingActivity(ProcessingActivityBasicDTO processingActivity) { this.processingActivity = processingActivity; }

    public RiskResponseDTO(BigInteger id, String name, String description, String riskRecommendation, boolean isReminderActive, int daysToReminderBefore, RiskSeverity riskLevel, AssetTypeDTO assetType, ProcessingActivityBasicDTO processingActivity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.riskRecommendation = riskRecommendation;
        this.isReminderActive = isReminderActive;
        this.daysToReminderBefore = daysToReminderBefore;
        this.riskLevel = riskLevel;
        this.assetType = assetType;
        this.processingActivity = processingActivity;
    }

    public RiskResponseDTO(BigInteger id, String name, String description, String riskRecommendation, boolean isReminderActive, int daysToReminderBefore, RiskSeverity riskLevel, String processingActivityName, BigInteger processingActivityId, boolean isSubProcessing) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.riskRecommendation = riskRecommendation;
        this.isReminderActive = isReminderActive;
        this.daysToReminderBefore = daysToReminderBefore;
        this.riskLevel = riskLevel;
        this.processingActivity = new ProcessingActivityBasicDTO(processingActivityId, processingActivityName,isSubProcessing);
    }

    public RiskResponseDTO(BigInteger id ,String name, String description, String riskRecommendation, boolean isReminderActive, int daysToReminderBefore,/*, int riskLevel*/ String processingActivityName, BigInteger processingActivityId, boolean isSubProcessing){
        this.id = id;
        this.name = name;
        this.description = description;
        this.riskRecommendation = riskRecommendation;
        this.isReminderActive = isReminderActive;
        this.daysToReminderBefore = daysToReminderBefore;
        //this.riskLevel = RiskSeverity.valueOf(riskLevel);
         this.processingActivity = new ProcessingActivityBasicDTO(processingActivityId, processingActivityName,isSubProcessing);
    }

    public RiskResponseDTO(){

    }
}
