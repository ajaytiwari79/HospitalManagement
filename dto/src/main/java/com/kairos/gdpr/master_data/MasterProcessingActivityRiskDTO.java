package com.kairos.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.gdpr.data_inventory.RiskDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterProcessingActivityRiskDTO {

    @NotNull(message = "Processing Activity Id can't be Null")
    private BigInteger id;

    @Valid
    private List<RiskDTO> risks=new ArrayList<>();

    @Valid
    private List<MasterProcessingActivityRiskDTO> subProcessingActivities=new ArrayList<>();

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public List<RiskDTO> getRisks() { return risks; }

    public void setRisks(List<RiskDTO> risks) { this.risks = risks; }

    public List<MasterProcessingActivityRiskDTO> getSubProcessingActivities() { return subProcessingActivities; }

    public void setSubProcessingActivities(List<MasterProcessingActivityRiskDTO> subProcessingActivities) { this.subProcessingActivities = subProcessingActivities; }
}
