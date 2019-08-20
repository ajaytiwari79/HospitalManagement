package com.kairos.persistence.repository.organization;

import com.kairos.dto.user.organization.hierarchy.OrganizationHierarchyFilterDTO;
import com.kairos.dto.user.staff.client.ClientFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;

import java.util.*;

/**
 * Created by oodles on 26/10/17.
 */
public interface CustomUnitGraphRepository {
    List<Map> getClientsWithFilterParameters(ClientFilterDTO clientFilterDTO, List<Long> citizenIds,
                                             Long organizationId, String imagePath, String skip,String moduleId);

    List<Map> getStaffWithFilters(Long unitId, Long parentOrganizationId, String moduleId,
                                  Map<FilterType, Set<String>> filters, String searchText, String imagePath);

    OrganizationBaseEntity getOrganizationHierarchyByFilters(long parentOrganizationId, OrganizationHierarchyFilterDTO organizationHierarchyFilterDTO);
}
