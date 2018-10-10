package com.kairos.persistence.repository.organization;

import com.kairos.enums.FilterType;
import com.kairos.dto.user.staff.client.ClientFilterDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 26/10/17.
 */
public interface CustomOrganizationGraphRepository {
    List<Map> getClientsWithFilterParameters(ClientFilterDTO clientFilterDTO, List<Long> citizenIds,
                                             Long organizationId, String imagePath, String skip,String moduleId);

    List<Map> getStaffWithFilters(Long unitId, Long parentOrganizationId,
                                  Map<FilterType, List<String>> filters, String searchText, String imagePath);
}
