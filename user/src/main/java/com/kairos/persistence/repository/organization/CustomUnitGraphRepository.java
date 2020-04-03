package com.kairos.persistence.repository.organization;

import com.kairos.dto.user.organization.hierarchy.OrganizationHierarchyFilterDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 26/10/17.
 */
public interface CustomUnitGraphRepository {

    List<Map<String, Object>> getOrganizationHierarchyByFilters(long parentOrganizationId, OrganizationHierarchyFilterDTO organizationHierarchyFilterDTO);
}
