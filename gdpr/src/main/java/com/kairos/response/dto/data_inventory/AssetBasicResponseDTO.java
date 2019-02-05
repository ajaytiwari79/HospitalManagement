package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.embeddables.ManagingOrganization;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetBasicResponseDTO {

    private Long id;

    private String name;

    private String description;

    private String hostingLocation;

    private ManagingOrganization managingDepartment;

    private boolean active;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id;
    }
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getHostingLocation() { return hostingLocation; }

    public void setHostingLocation(String hostingLocation) { this.hostingLocation = hostingLocation; }

    public ManagingOrganization getManagingDepartment() { return managingDepartment; }

    public void setManagingDepartment(ManagingOrganization managingDepartment) { this.managingDepartment = managingDepartment; }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
}
