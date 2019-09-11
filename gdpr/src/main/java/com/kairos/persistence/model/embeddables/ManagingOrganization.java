package com.kairos.persistence.model.embeddables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManagingOrganization {


    @NotNull
    private Long managingOrgId;

    @NotBlank(message = "error.message.managingDepartment.name.notNull")
    private String managingOrgName;

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

}
