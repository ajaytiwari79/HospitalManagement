package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterProcessingActivityRiskDTO {

    @NotNull(message = "Processing Activity Id can't be Null")
    private Long id;

    @Valid
    private List<BasicRiskDTO> risks=new ArrayList<>();

    @Valid
    private List<MasterProcessingActivityRiskDTO> subProcessingActivities=new ArrayList<>();

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public List<BasicRiskDTO> getRisks() { return risks; }

    public void setRisks(List<BasicRiskDTO> risks) { this.risks = risks; }

    public List<MasterProcessingActivityRiskDTO> getSubProcessingActivities() { return subProcessingActivities; }

    public void setSubProcessingActivities(List<MasterProcessingActivityRiskDTO> subProcessingActivities) { this.subProcessingActivities = subProcessingActivities; }
}
