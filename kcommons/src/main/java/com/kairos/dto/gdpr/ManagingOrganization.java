package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManagingOrganization {


    @NotNull(message = "error.message.id.notnull")
    private Long managingOrgId;

    @NotBlank(message = "error.message.managingDepartment.notNull")
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
