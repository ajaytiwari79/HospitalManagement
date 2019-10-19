package com.kairos.dto.user.organization;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by prerna on 15/11/17.
 */
@Getter
@Setter
public class OrganizationServiceDTO {
    @NotEmpty(message = "error.Organization.Service.customName.notEmptyOrNotNull") @NotNull(message = "error.Organization.Service.customName.notEmptyOrNotNull")
    private String customName;
    private Long id;
    private String name;
    private List<OrganizationServiceDTO> organizationSubServices;

}
