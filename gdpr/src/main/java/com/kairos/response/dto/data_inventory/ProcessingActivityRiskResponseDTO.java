package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.common.RiskBasicResponseDTO;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingActivityRiskResponseDTO {

    private Long id;
    private String name;
    private Boolean mainParent;
    private List<RiskBasicResponseDTO> risks;
    private List<ProcessingActivityRiskResponseDTO> processingActivities;

    public ProcessingActivityRiskResponseDTO(Long id, String name, Boolean mainParent, List<RiskBasicResponseDTO> risks) {
        this.id = id;
        this.name = name;
        this.mainParent = mainParent;
        this.risks = risks;
    }

    public ProcessingActivityRiskResponseDTO() {
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Boolean getMainParent() { return mainParent; }

    public void setMainParent(Boolean mainParent) { this.mainParent = mainParent; }

    public List<ProcessingActivityRiskResponseDTO> getProcessingActivities() { return processingActivities; }

    public void setProcessingActivities(List<ProcessingActivityRiskResponseDTO> processingActivities) { this.processingActivities = processingActivities; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<RiskBasicResponseDTO> getRisks() { return risks; }

    public void setRisks(List<RiskBasicResponseDTO> risks) { this.risks = risks; }
}
