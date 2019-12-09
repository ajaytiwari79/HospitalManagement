package com.kairos.persistence.repository.organization;

import com.kairos.dto.user.organization.hierarchy.OrganizationHierarchyFilterDTO;
import com.kairos.dto.user.staff.client.ClientFilterDTO;
import com.kairos.enums.FilterType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by oodles on 26/10/17.
 */
public interface CustomUnitGraphRepository {
    List<Map> getClientsWithFilterParameters(ClientFilterDTO clientFilterDTO, List<Long> citizenIds,
                                             Long organizationId, String imagePath, String skip,String moduleId);



    List<Map<String, Object>> getOrganizationHierarchyByFilters(long parentOrganizationId, OrganizationHierarchyFilterDTO organizationHierarchyFilterDTO);
}
