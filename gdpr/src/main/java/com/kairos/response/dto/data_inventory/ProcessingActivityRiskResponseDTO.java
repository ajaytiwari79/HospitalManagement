package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.common.RiskResponseDTO;

import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingActivityRiskResponseDTO {


    private BigInteger id;
    private String name;
    private List<RiskResponseDTO> risks;
    private List<ProcessingActivityRiskResponseDTO> subProcessingActivities;

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public List<ProcessingActivityRiskResponseDTO> getSubProcessingActivities() { return subProcessingActivities; }

    public void setSubProcessingActivities(List<ProcessingActivityRiskResponseDTO> subProcessingActivities) { this.subProcessingActivities = subProcessingActivities; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<RiskResponseDTO> getRisks() { return risks; }

    public void setRisks(List<RiskResponseDTO> risks) { this.risks = risks; }
}
