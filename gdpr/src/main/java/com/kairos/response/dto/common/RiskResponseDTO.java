package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.Staff;
import com.kairos.enums.RiskSeverity;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicDTO;
import com.kairos.response.dto.master_data.AssetTypeDTO;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
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

    //used in native query constructor result
    public RiskResponseDTO(BigInteger id ,String name, String description, String riskRecommendation, boolean isReminderActive, int daysToReminderBefore, int riskLevel, String processingActivityName, BigInteger processingActivityId, boolean isSubProcessingActivity){
        this.id = id;
        this.name = name;
        this.description = description;
        this.riskRecommendation = riskRecommendation;
        this.isReminderActive = isReminderActive;
        this.daysToReminderBefore = daysToReminderBefore;
        this.riskLevel = RiskSeverity.values()[riskLevel];
        this.processingActivity = new ProcessingActivityBasicDTO(Long.valueOf(processingActivityId.toString()), processingActivityName,isSubProcessingActivity);

    }

    //used in native query constructor result
    public RiskResponseDTO(BigInteger id ,String name, String description, String riskRecommendation, boolean isReminderActive, int daysToReminderBefore, int riskLevel,  BigInteger assetTypeId,String assetTypeName,boolean isSubAssetType){
        this.id = id;
        this.name = name;
        this.description = description;
        this.riskRecommendation = riskRecommendation;
        this.isReminderActive = isReminderActive;
        this.daysToReminderBefore = daysToReminderBefore;
        this.riskLevel = RiskSeverity.values()[riskLevel];
        this.assetType = new AssetTypeDTO(Long.valueOf(assetTypeId.toString()),assetTypeName,isSubAssetType);
    }
}
