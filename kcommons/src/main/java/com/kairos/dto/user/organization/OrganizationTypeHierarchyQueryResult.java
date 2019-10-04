package com.kairos.dto.user.organization;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrganizationTypeHierarchyQueryResult {

    private List<Map<String,Object>> organizationTypes;

    public List<Map<String, Object>> getOrganizationTypes() {
        return Optional.ofNullable(organizationTypes).orElse(Collections.emptyList());
    }

    public void setOrganizationTypes(List<Map<String, Object>> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }
}
