package com.planner.component.rest_client;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.dto.planner.solverconfig.DefaultDataDTO;
import com.kairos.dto.user.organization.OrganizationServiceDTO;
import com.kairos.enums.rest_client.RestClientUrlType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class IntegrationService {
    @Inject
    GenericRestClient genericRestClient;

    public DefaultDataDTO getDefaultDataForSolverConfig(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/get_default_data_for_solver_cofig", null,true, new ParameterizedTypeReference<RestTemplateResponseEnvelope<DefaultDataDTO>>(){});
    }

    public List<OrganizationServiceDTO> getOrganisationServiceByunitId(Long unitId) {
        return genericRestClient.publishRequest(null, unitId, RestClientUrlType.UNIT, HttpMethod.GET, "/get_organisation_services_by_unit", null,false, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<OrganizationServiceDTO>>>(){});
    }
}

