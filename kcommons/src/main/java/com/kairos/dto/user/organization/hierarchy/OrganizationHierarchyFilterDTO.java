package com.kairos.dto.user.organization.hierarchy;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class OrganizationHierarchyFilterDTO {

    private Set<Long> organizationTypeIds;
    private Set<Long> organizationSubTypeIds;
    private Set<Long> organizationServiceIds;
    private Set<Long> organizationSubServiceIds;
    private Set<Long> organizationAccountTypeIds;
}
