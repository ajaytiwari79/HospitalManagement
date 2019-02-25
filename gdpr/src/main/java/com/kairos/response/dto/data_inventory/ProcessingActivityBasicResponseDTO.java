package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.ManagingOrganization;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProcessingActivityBasicResponseDTO {

    private Long id;
    private String name;
    private String description;
    private ManagingOrganization managingDepartment;
    private List<ProcessingActivityBasicResponseDTO> subProcessingActivities=new ArrayList<>();
    private Boolean suggested;

    public ProcessingActivityBasicResponseDTO() {

    }

    public ProcessingActivityBasicResponseDTO(Long id, String name, String description, Long managingOrgId, String managingOrgName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.managingDepartment = new ManagingOrganization(managingOrgId,managingOrgName);
    }

    public Boolean getSuggested() { return suggested; }

    public void setSuggested(Boolean suggested) { this.suggested = suggested; }

    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public void setManagingDepartment(ManagingOrganization managingDepartment) { this.managingDepartment = managingDepartment; }

    public Long getId() { return id;}

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public List<ProcessingActivityBasicResponseDTO> getSubProcessingActivities() { return subProcessingActivities; }

    public void setSubProcessingActivities(List<ProcessingActivityBasicResponseDTO> subProcessingActivities) { this.subProcessingActivities = subProcessingActivities; }
}
