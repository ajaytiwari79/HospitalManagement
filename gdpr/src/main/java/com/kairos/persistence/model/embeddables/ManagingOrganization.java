package com.kairos.persistence.model.embeddables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.data_inventory.assessment.SelectedChoice;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
public class ManagingOrganization {


    @NotNull
    private Long managingOrgId;

    @NotBlank(message = "ManagingOrganization name can't be empty")
    private String managingOrgName;

    public Long getManagingOrgId() {
        return managingOrgId;
    }

    public ManagingOrganization(@NotNull Long managingOrgId, @NotBlank(message = "ManagingOrganization name can't be empty") String name) {
        this.managingOrgId = managingOrgId;
        this.managingOrgName = name;
    }

    public void setManagingOrgId(Long managingOrgId) {
        this.managingOrgId = managingOrgId;
    }

    public String getName() { return managingOrgName; }

    public void setName(String name) { this.managingOrgName = name; }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagingOrganization that = (ManagingOrganization) o;
        return Objects.equals(managingOrgId, that.managingOrgId) &&
                Objects.equals(managingOrgName, that.managingOrgName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(managingOrgId, managingOrgName);
    }

    public ManagingOrganization() {
    }
}
