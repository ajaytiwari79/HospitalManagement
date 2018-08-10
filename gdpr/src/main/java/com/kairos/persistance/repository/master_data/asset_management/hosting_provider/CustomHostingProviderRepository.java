package com.kairos.persistance.repository.master_data.asset_management.hosting_provider;

import com.kairos.response.dto.common.HostingProviderResponseDTO;

import java.util.List;

public interface CustomHostingProviderRepository {

    List<HostingProviderResponseDTO> getAllNotInheritedHostingProviderFromParentOrgAndUnitHostingProvider(Long countryId, Long parentOrganizationId, Long organizationId);

}
