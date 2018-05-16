package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.enums.FilterEntityType;
import com.kairos.response.dto.web.client.ClientFilterDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 26/10/17.
 */
public interface CustomOrganizationGraphRepository {
    List<Map> getClientsWithFilterParameters(ClientFilterDTO clientFilterDTO, List<Long> citizenIds,
                                             Long organizationId, String imagePath, String skip,String moduleId);


    List<Map> getStaffWithFilters(Long unitId, Long parentOrganizationId, Boolean fetchStaffHavingUnitPosition,
                                         Map<FilterEntityType, List<String>> filters, String searchText, String imagePath);
}
