package com.kairos.dto.user.organization;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Getter
@Setter
public class OrganizationTypeHierarchyQueryResult {

    private List<Map<String,Object>> organizationTypes;

}
