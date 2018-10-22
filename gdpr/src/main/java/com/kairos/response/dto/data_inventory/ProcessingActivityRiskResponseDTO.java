package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.common.RiskBasicResponseDTO;

import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingActivityRiskResponseDTO {

    private BigInteger id;
    private String name;
    private Boolean mainParent;
    private List<RiskBasicResponseDTO> risks;
    private List<ProcessingActivityRiskResponseDTO> processingActivities;

    public ProcessingActivityRiskResponseDTO(BigInteger id, String name, Boolean mainParent, List<RiskBasicResponseDTO> risks) {
        this.id = id;
        this.name = name;
        this.mainParent = mainParent;
        this.risks = risks;
    }

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public Boolean getMainParent() { return mainParent; }

    public void setMainParent(Boolean mainParent) { this.mainParent = mainParent; }

    public List<ProcessingActivityRiskResponseDTO> getProcessingActivities() { return processingActivities; }

    public void setProcessingActivities(List<ProcessingActivityRiskResponseDTO> processingActivities) { this.processingActivities = processingActivities; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<RiskBasicResponseDTO> getRisks() { return risks; }

    public void setRisks(List<RiskBasicResponseDTO> risks) { this.risks = risks; }
}
