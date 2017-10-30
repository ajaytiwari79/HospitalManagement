package com.kairos.persistence.repository.organization;

import com.kairos.response.dto.web.ClientFilterDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 26/10/17.
 */
public interface CustomOrganizationGraphRepository {
    List<Map> getClientsWithFilterParameters(ClientFilterDTO clientFilterDTO, List<Long> citizenIds, Long organizationId, String imagePath, String skip);

}
