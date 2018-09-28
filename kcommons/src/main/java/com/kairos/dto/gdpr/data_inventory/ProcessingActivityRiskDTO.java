package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingActivityRiskDTO {

    @NotNull(message = "id can't be Null")
    private BigInteger id;

    @Valid
    @NotEmpty
    private List<OrganizationLevelRiskDTO> risks;

    private List<ProcessingActivityRiskDTO> subProcessingActivities=new ArrayList<>();

    public List<ProcessingActivityRiskDTO> getSubProcessingActivities() { return subProcessingActivities; }

    public void setSubProcessingActivities(List<ProcessingActivityRiskDTO> subProcessingActivities) { this.subProcessingActivities = subProcessingActivities; }

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public List<OrganizationLevelRiskDTO> getRisks() { return risks; }

    public void setRisks(List<OrganizationLevelRiskDTO> risks) { this.risks = risks; }

}
