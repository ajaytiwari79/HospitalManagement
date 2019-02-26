package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.ManagingOrganization;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class ProcessingActivityBasicResponseDTO {

    private BigInteger id;
    private String name;
    private String description;
    private ManagingOrganization managingDepartment;
    private List<ProcessingActivityBasicResponseDTO> subProcessingActivities=new ArrayList<>();
    private Boolean suggested;


    public Boolean getSuggested() { return suggested; }

    public void setSuggested(Boolean suggested) { this.suggested = suggested; }

    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public void setManagingDepartment(ManagingOrganization managingDepartment) { this.managingDepartment = managingDepartment; }

    public BigInteger getId() { return id;}

    public void setId(BigInteger id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public List<ProcessingActivityBasicResponseDTO> getSubProcessingActivities() { return subProcessingActivities; }

    public void setSubProcessingActivities(List<ProcessingActivityBasicResponseDTO> subProcessingActivities) { this.subProcessingActivities = subProcessingActivities; }
}
