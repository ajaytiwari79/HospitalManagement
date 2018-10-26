package com.kairos.response.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.response.dto.common.RiskBasicResponseDTO;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterProcessingActivityRiskResponseDTO {


    private BigInteger id;
    private String name;
    private Boolean mainParent;
    private List<RiskBasicResponseDTO> risks=new ArrayList<>();
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;

    private List<MasterProcessingActivityRiskResponseDTO>  processingActivities=new ArrayList<>();

    public MasterProcessingActivityRiskResponseDTO() {
    }

    public MasterProcessingActivityRiskResponseDTO(BigInteger id, String name, boolean mainParent, List<RiskBasicResponseDTO> risks, LocalDate suggestedDate, SuggestedDataStatus suggestedDataStatus) {
        this.id = id;
        this.name = name;
        this.mainParent = mainParent;
        this.risks=risks;
       this.suggestedDate= suggestedDate;
       this.suggestedDataStatus=suggestedDataStatus;
    }

    public LocalDate getSuggestedDate() { return suggestedDate; }

    public void setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; }

    public SuggestedDataStatus getSuggestedDataStatus() { return suggestedDataStatus; }

    public void setSuggestedDataStatus(SuggestedDataStatus suggestedDataStatus) { this.suggestedDataStatus = suggestedDataStatus; }

    public List<MasterProcessingActivityRiskResponseDTO> getProcessingActivities() { return processingActivities; }

    public void setProcessingActivities(List<MasterProcessingActivityRiskResponseDTO> processingActivities) { this.processingActivities = processingActivities; }

    public BigInteger getId() { return id; }

    public Boolean getMainParent() { return mainParent; }

    public void setMainParent(Boolean mainParent) { this.mainParent = mainParent; }

    public void setId(BigInteger id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<RiskBasicResponseDTO> getRisks() { return risks; }

    public void setRisks(List<RiskBasicResponseDTO> risks) { this.risks = risks; }
}
