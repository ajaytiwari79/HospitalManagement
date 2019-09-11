package com.kairos.response.dto.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.ManagingOrganization;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
@NoArgsConstructor
public class ProcessingActivityBasicResponseDTO {

    private Long id;
    private String name;
    private String description;
    private ManagingOrganization managingDepartment;
    private List<ProcessingActivityBasicResponseDTO> subProcessingActivities=new ArrayList<>();
    private Boolean suggested;

    public ProcessingActivityBasicResponseDTO(Long id, String name, String description, Long managingOrgId, String managingOrgName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.managingDepartment = new ManagingOrganization(managingOrgId,managingOrgName);
    }
}
