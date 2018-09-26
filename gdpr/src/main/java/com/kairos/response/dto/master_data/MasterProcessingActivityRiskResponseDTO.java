package com.kairos.response.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.response.dto.common.RiskResponseDTO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterProcessingActivityRiskResponseDTO {


    private BigInteger id;
    private String name;
    private boolean mainParent;
    private List<RiskResponseDTO> risks=new ArrayList<>();
    private List<MasterProcessingActivityRiskResponseDTO>  processingActivities=new ArrayList<>();

    public MasterProcessingActivityRiskResponseDTO() {
    }

    public MasterProcessingActivityRiskResponseDTO(BigInteger id, String name, boolean mainParent, List<RiskResponseDTO> risks) {
        this.id = id;
        this.name = name;
        this.mainParent = mainParent;
        this.risks=risks;
    }

    public List<MasterProcessingActivityRiskResponseDTO> getProcessingActivities() { return processingActivities; }

    public void setProcessingActivities(List<MasterProcessingActivityRiskResponseDTO> processingActivities) { this.processingActivities = processingActivities; }

    public BigInteger getId() { return id; }

    public boolean isMainParent() { return mainParent; }

    public void setMainParent(boolean mainParent) { this.mainParent = mainParent; }

    public void setId(BigInteger id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<RiskResponseDTO> getRisks() { return risks; }

    public void setRisks(List<RiskResponseDTO> risks) { this.risks = risks; }
}
